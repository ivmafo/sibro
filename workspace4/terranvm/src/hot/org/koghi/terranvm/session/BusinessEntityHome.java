package org.koghi.terranvm.session;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.TreeMap;

import javax.faces.context.FacesContext;
import javax.imageio.stream.FileImageInputStream;
import javax.persistence.Query;

import org.hibernate.Session;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.TransactionPropagationType;
import org.jboss.seam.annotations.Transactional;
import org.jboss.seam.annotations.datamodel.DataModel;
import org.jboss.seam.core.Expressions;
import org.jboss.seam.framework.EntityHome;
import org.jboss.seam.international.StatusMessage;
import org.jboss.seam.international.StatusMessage.Severity;
import org.koghi.terranvm.entity.Action;
import org.koghi.terranvm.entity.Address;
import org.koghi.terranvm.entity.BillingResolution;
import org.koghi.terranvm.entity.BusinessEntity;
import org.koghi.terranvm.entity.BusinessEntityContact;
import org.koghi.terranvm.entity.BusinessEntityType;
import org.koghi.terranvm.entity.BusinessLine;
import org.koghi.terranvm.entity.ConsecutiveAccountsBilling;
import org.koghi.terranvm.entity.ConsecutiveCollectionAccount;
import org.koghi.terranvm.entity.ConsecutiveCreditNotes;
import org.koghi.terranvm.entity.Contact;
import org.koghi.terranvm.entity.EconomicActivity;
import org.koghi.terranvm.entity.EconomicSector;
import org.koghi.terranvm.entity.Image;
import org.koghi.terranvm.entity.MakerChecker;
import org.koghi.terranvm.entity.PhoneNumber;
import org.koghi.terranvm.entity.Project;
import org.koghi.terranvm.entity.Region;
import org.koghi.terranvm.entity.User_Terranvm;
import org.richfaces.component.html.HtmlExtendedDataTable;
import org.richfaces.event.UploadEvent;
import org.richfaces.model.UploadItem;
import org.richfaces.model.selection.Selection;

@Name("businessEntityHome")
public class BusinessEntityHome extends EntityHome<BusinessEntity> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	// Una ruta del logo de la entidad de forma temporal
	private String imagePathFullTemp = "";

	@In(create = true)
	EconomicActivityHome economicActivityHome;
	@In(create = true)
	@Out(required = false)
	PhoneNumberHome phoneNumberHome;
	@In(create = true)
	@Out(required = false)
	AddressHome addressHome;

	@In(create = true)
	BusinessEntityHome businessEntityHome;

	public List<EconomicSector> sectores;

	private EconomicSector economicSector;

	private Selection selectionLegalRepresentative;
	private Contact legalRepresentative;
	@DataModel
	private List<Contact> legalRepresentatives;
	private String tableState;
	private TreeMap<String, Object[]> businessEntityExistingTree;

	@In(required = false)
	@Out(required = false)
	private HtmlExtendedDataTable tableLegalRepresentativeBind;
	private Region selectedRegion;

	// Imagen
	// private byte[] data = null;
	// private String fileName;
	// private String contenType;
	// private String messegeTypeError = null;
	private ArrayList<Image> files = new ArrayList<Image>();
	private int uploadsAvailable = 1;
	private boolean autoUpload = true;
	private boolean useFlash = false;
	// Imagen

	// validacion id_number
	private String messageIdNumber;

	private List<EconomicActivity> economitActivityList;

	@In(required = false)
	private User_Terranvm user;

	private ArrayList<String> responsabilidadesTributariasGroupCero;
	private String responsabilidadTributariaGroupOne;
	private String responsabilidadTributariaGroupTwo;

	@Out(scope = ScopeType.CONVERSATION, required = false)
	@In(required = false)
	private List<PhoneNumber> phoneNumbersCache;

	@Out(scope = ScopeType.CONVERSATION, required = false)
	@In(required = false)
	private List<Address> addressCache;

	public EconomicSector getEconomicSector() {
		if (this.instance != null
				&& this.instance.getEconomicActivity() != null
				&& this.economicSector == null) {
			this.economicSector = this.instance.getEconomicActivity()
					.getEconomicSector();
		}
		return economicSector;
	}

	public void setEconomicSector(EconomicSector economicSector) {
		this.economicSector = economicSector;
	}

	@SuppressWarnings("unchecked")
	public List<EconomicSector> getSectores() {
		return this.getEntityManager().createQuery("from EconomicSector")
				.getResultList();
	}

	public void setSectores(List<EconomicSector> sectores) {
		this.sectores = sectores;
	}

	/**
	 * Se listan las regiones asociadas a al pais de colombia
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Region> getSelectRegionList() {
		return this.getEntityManager().createQuery("from Region r  where r.country.abbreviation='CO' order by name").getResultList();

	}

	public Region getSelectedRegion() {
		if (this.instance != null && this.instance.getCity() != null) {
			this.selectedRegion = this.instance.getCity().getRegion();
			// this.selectedCountry = this.selectedRegion.getCountry();
			// System.out.println(this.selectedRegion.getName()+" distinto de null");
		}
		return selectedRegion;
	}

	public void setSelectedRegion(Region selectedRegion) {
		if (!selectedRegion.getCities().isEmpty()){
			getInstance().setCity(selectedRegion.getCities().iterator().next());
		}else
			getInstance().setCity(null);
		this.selectedRegion = selectedRegion;
	}

	private boolean nitSelected;

	@Override
	public String persist() {
		if (this.responsabilidadTributariaGroupOne != null)
			this.responsabilidadesTributariasGroupCero
					.add(this.responsabilidadTributariaGroupOne);
		this.responsabilidadesTributariasGroupCero
				.add(this.responsabilidadTributariaGroupTwo);

		if (this.responsabilidadesTributariasGroupCero != null
				&& !this.responsabilidadesTributariasGroupCero.isEmpty()) {
			int cont = 0;
			String responsabilidades = "";
			for (int m = 0; m < this.responsabilidadesTributariasGroupCero
					.size(); m++) {
				if (this.responsabilidadesTributariasGroupCero.get(m) != null) {
					cont = m;
					responsabilidades = new String(
							this.responsabilidadesTributariasGroupCero
									.get(cont));
					break;
				}
			}
			for (int i = cont + 1; i < this.responsabilidadesTributariasGroupCero
					.size(); i++) {
				responsabilidades += ","
						+ this.responsabilidadesTributariasGroupCero.get(i);
			}
			this.instance.setTaxLiabilities(responsabilidades);

		}
		try {
		super.persist();
		} catch (Exception e){
			e.printStackTrace();
			printNext(e);
		}
		for (PhoneNumber phoneNumber : getInstance().getPhoneNumbers()) {
			phoneNumber.setBusinessEntity(this.instance);
			getEntityManager().persist(phoneNumber);
		}
		for (Address address : getInstance().getAddresses()) {
			address.setBusinessEntity(this.instance);
			getEntityManager().persist(address);
		}
		String m = super.persist();
		return m;
	}

	private void printNext(Throwable throwable) {
		if (throwable == null)
			return;
		else if (throwable instanceof SQLException)
			((SQLException)throwable).getNextException().printStackTrace();
		else
			printNext(throwable.getCause());
	}

	// public void clean(){
	// PhoneNumber phoneNumber = new PhoneNumber();
	// //phoneNumber.setBusinessEntity(null);
	//
	// //getEntityManager().persist(phoneNumber);
	//
	// Address address = new Address();
	// //address.setBusinessEntity(null);
	// //getEntityManager().persist(address);
	// }

	@Override
	public String update() {
		if (this.responsabilidadTributariaGroupOne != null)
			this.responsabilidadesTributariasGroupCero
					.add(this.responsabilidadTributariaGroupOne);
		this.responsabilidadesTributariasGroupCero
				.add(this.responsabilidadTributariaGroupTwo);

		if (this.responsabilidadesTributariasGroupCero != null
				&& !this.responsabilidadesTributariasGroupCero.isEmpty()) {
			int cont = 0;
			String responsabilidades = "";
			for (int m = 0; m < this.responsabilidadesTributariasGroupCero
					.size(); m++) {
				if (this.responsabilidadesTributariasGroupCero.get(m) != null) {
					cont = m;
					responsabilidades = new String(
							this.responsabilidadesTributariasGroupCero
									.get(cont));
					break;
				}
			}
			for (int i = cont + 1; i < this.responsabilidadesTributariasGroupCero
					.size(); i++) {
				responsabilidades += ","
						+ this.responsabilidadesTributariasGroupCero.get(i);
			}

			this.instance.setTaxLiabilities(responsabilidades);
		}
		new MakerCheckerHome().persistObject(getInstance(), getInstance()
				.getProjects().toArray());
		updatedMessage();
		return "updated";

	}

	public ArrayList<String> getResponsabilidadesTributariasGroupCero() {
		if (this.instance != null && this.instance.getTaxLiabilities() != null) {

			this.responsabilidadesTributariasGroupCero = new ArrayList<String>();

			for (String responsabilidadCodigo : this.instance
					.getTaxLiabilities().split(",")) {
				if (!(responsabilidadCodigo.equals("06")
						|| responsabilidadCodigo.equals("05")
						|| responsabilidadCodigo.equals("04")
						|| responsabilidadCodigo.equals("11")
						|| responsabilidadCodigo.equals("12")
						|| responsabilidadCodigo.equals("20")
						|| responsabilidadCodigo.equals("21")
						|| responsabilidadCodigo.equals("22") || responsabilidadCodigo
						.equals("23"))) {
					this.responsabilidadesTributariasGroupCero
							.add(responsabilidadCodigo);
				}
			}
		}
		return responsabilidadesTributariasGroupCero;
	}

	public void setResponsabilidadesTributariasGroupCero(
			ArrayList<String> responsabilidadesTributariasGroupCero) {
		this.responsabilidadesTributariasGroupCero = responsabilidadesTributariasGroupCero;
	}

	public String getResponsabilidadTributariaGroupOne() {
		if (this.instance != null && this.instance.getTaxLiabilities() != null) {
			if (this.instance.getTaxLiabilities().contains("04"))
				this.responsabilidadTributariaGroupOne = "04";
			if (this.instance.getTaxLiabilities().contains("05"))
				this.responsabilidadTributariaGroupOne = "05";
			if (this.instance.getTaxLiabilities().contains("06"))
				this.responsabilidadTributariaGroupOne = "06";
		}
		return responsabilidadTributariaGroupOne;
	}

	public void setResponsabilidadTributariaGroupOne(
			String responsabilidadTributariaGroupOne) {
		this.responsabilidadTributariaGroupOne = responsabilidadTributariaGroupOne;
	}

	public String getResponsabilidadTributariaGroupTwo() {
		if (this.instance != null && this.instance.getTaxLiabilities() != null) {
			if (this.instance.getTaxLiabilities().contains("11"))
				this.responsabilidadTributariaGroupTwo = "11";
			if (this.instance.getTaxLiabilities().contains("12"))
				this.responsabilidadTributariaGroupTwo = "12";
			if (this.instance.getTaxLiabilities().contains("20"))
				this.responsabilidadTributariaGroupTwo = "20";
			if (this.instance.getTaxLiabilities().contains("21"))
				this.responsabilidadTributariaGroupTwo = "21";
			if (this.instance.getTaxLiabilities().contains("22"))
				this.responsabilidadTributariaGroupTwo = "22";
			if (this.instance.getTaxLiabilities().contains("23"))
				this.responsabilidadTributariaGroupTwo = "23";
		}
		return responsabilidadTributariaGroupTwo;
	}

	public void setResponsabilidadTributariaGroupTwo(
			String responsabilidadTributariaGroupTwo) {
		this.responsabilidadTributariaGroupTwo = responsabilidadTributariaGroupTwo;
	}

	public void setBusinessEntityId(Integer id) {
		setId(id);
	}

	public Integer getBusinessEntityId() {
		return (Integer) getId();
	}

	@Override
	protected BusinessEntity createInstance() {
		BusinessEntity businessEntity = new BusinessEntity();
		return businessEntity;
	}

	public void load() {
		if (isIdDefined()) {
			wire();
		}
	}

	public void wire() {
		getInstance();
		// clean();
		this.checkNit();
	}

	public boolean isWired() {
		// Contact con = this.getLegalRepresentative();
		// if (con == null)
		// return false;
		if (getInstance().getPhoneNumbers() == null
				|| getInstance().getPhoneNumbers().isEmpty())
			return false;
		if (getInstance().getAddresses() == null
				|| getInstance().getAddresses().isEmpty())
			return false;
		return true;
	}

	public BusinessEntity getDefinedInstance() {
		return isIdDefined() ? getInstance() : null;
	}

	public List<PhoneNumber> getPhoneNumbers() {
		return getInstance() == null ? null : new ArrayList<PhoneNumber>(
				getInstance().getPhoneNumbers());
	}

	public List<Address> getAddresses() {
		return getInstance() == null ? null : new ArrayList<Address>(
				getInstance().getAddresses());
	}

	public List<Project> getProjects() {
		return getInstance() == null ? null : new ArrayList<Project>(
				getInstance().getProjects());
	}

	public List<BillingResolution> getBillingResolutions() {
		return getInstance() == null ? null : new ArrayList<BillingResolution>(
				getInstance().getBillingResolutions());
	}

	public List<BusinessEntityType> getEntityTypes() {
		return getInstance() == null ? null
				: new ArrayList<BusinessEntityType>(getInstance()
						.getEntityTypes());
	}

	public List<BusinessLine> getBusinessLines() {
		return getInstance() == null ? null : new ArrayList<BusinessLine>(
				getInstance().getBusinessLines());
	}

	public List<BusinessEntityContact> getBusinessEntityContacts() {
		return getInstance() == null ? null
				: new ArrayList<BusinessEntityContact>(getInstance()
						.getBusinessEntityContacts());
	}

	public List<ConsecutiveAccountsBilling> getConsecutiveAccountsBillings() {
		return getInstance() == null ? null
				: new ArrayList<ConsecutiveAccountsBilling>(getInstance()
						.getConsecutiveAccountsBillings());
	}

	public List<ConsecutiveCreditNotes> getConsecutiveCreditNoteses() {
		return getInstance() == null ? null
				: new ArrayList<ConsecutiveCreditNotes>(getInstance()
						.getConsecutiveCreditNoteses());
	}

	public List<ConsecutiveCollectionAccount> getConsecutiveCollectionAccounts() {
		return getInstance() == null ? null
				: new ArrayList<ConsecutiveCollectionAccount>(getInstance()
						.getConsecutiveCollectionAccounts());
	}

	public void checkNit() {
		if (this.getInstance().getIdType() == 31)
			this.nitSelected = true;
		else
			this.nitSelected = false;
		//checkDigitCalculation();
	}

	public boolean isNitSelected() {
		return nitSelected;
	}

	public void setNitSelected(boolean nitSelected) {
		this.nitSelected = nitSelected;
	}

	public boolean businessEntityListInApprove(BusinessEntity businessEntity) {
		return new MakerCheckerHome().isObjectInMakerChecker(businessEntity);
	}

	public void updateInstanceMaker(int makerCheckerId) {
		setInstance((BusinessEntity) new MakerCheckerHome()
				.getInstance(makerCheckerId));
		setInstance(getEntityManager().merge(getInstance()));
	}

	@Transactional
	public void approveChange() {
		setInstance(getEntityManager().merge(getInstance()));
		joinTransaction();
		getEntityManager().flush();
		new MakerCheckerHome().deleteMaker(this.getInstance());
		getFacesMessages().addFromResourceBundle(Severity.INFO,
				"#{messages.Successful_passage}", "ApproveSuccessfully");
	}

	public void cancelChange() {
		new MakerCheckerHome().deleteMaker(this.getInstance());
		getFacesMessages().addFromResourceBundle(Severity.INFO,
				"#{messages.Successful_cancellation}", "CancelSuccessfully");
	}

	public List<Contact> autocompleteTerceros(Object suggest) {

		String pref = (String) suggest;
		List<Contact> result = new ArrayList<Contact>();
		try {
			Query q = this.getEntityManager().createQuery("from Contact c");
			List<Contact> l = (List<Contact>) q.getResultList();
			Iterator<Contact> iterator = l.iterator();
			while (iterator.hasNext()) {
				Contact elem = ((Contact) iterator.next());
				String name = elem.getFirstName() + " " + elem.getLastName();
				if (name.toLowerCase().indexOf(pref.toLowerCase()) != -1
						&& elem.getContactType().getId() == 3) {
					result.add(elem);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	public Contact getLegalRepresentative() {
		if (this.legalRepresentative == null) {
			List<Contact> list = this.instance.getContactos();
			for (Contact contact : list) {
				if (contact.getContactType().getId() == 3) {
					this.legalRepresentative = contact;
					break;
				}
			}
		}
		return legalRepresentative;
	}

	public void setLegalRepresentative(Contact legalRepresentative) {
		this.legalRepresentative = legalRepresentative;
	}

	public List<Contact> getLegalRepresentatives() {
		if (this.legalRepresentatives == null) {
			legalRepresentatives = new ArrayList<Contact>();
			try {
				Query q = this.getEntityManager().createQuery(
						"from Contact c where c.contactType.id = 3");
				List<Contact> l = (List<Contact>) q.getResultList();
				Iterator<Contact> iterator = l.iterator();
				while (iterator.hasNext()) {
					Contact elem = ((Contact) iterator.next());
					legalRepresentatives.add(elem);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return legalRepresentatives;
		} else {

			return legalRepresentatives;
		}

	}

	public void onSelectionChanged() {
		if (selectionLegalRepresentative != null) {
			System.out.println("Selected keys: ");
			Iterator it = selectionLegalRepresentative.getKeys();
			while (it.hasNext()) {
				Object key = it.next();
				System.out.println("key: " + key);
				System.out.println("tableLegalRepresentativeBind state"
						+ tableState);
				tableLegalRepresentativeBind.setRowKey(key);
				if (tableLegalRepresentativeBind.isRowAvailable()) {
					if (this.instance.getContactos().contains(
							legalRepresentative))
						this.instance.getContactos()
								.remove(legalRepresentative);
					setLegalRepresentative((Contact) tableLegalRepresentativeBind
							.getRowData());
					this.instance.getContactos().add(legalRepresentative);
				}
			}
		} else {
			System.out.println("No selectionLegalRepresentative is set.");
		}
	}

	public void setLegalRepresentatives(List<Contact> legalRepresentatives) {
		this.legalRepresentatives = legalRepresentatives;
	}

	public String getTableState() {
		return tableState;
	}

	public void setTableState(String tableState) {
		this.tableState = tableState;
	}

	public Selection getSelectionLegalRepresentative() {
		return selectionLegalRepresentative;

	}

	public void setSelectionLegalRepresentative(
			Selection selectionLegalRepresentative) {
		this.selectionLegalRepresentative = selectionLegalRepresentative;
	}

	public HtmlExtendedDataTable getTableLegalRepresentativeBind() {
		return tableLegalRepresentativeBind;
	}

	public void setTableLegalRepresentativeBind(
			HtmlExtendedDataTable tableLegalRepresentativeBind) {
		this.tableLegalRepresentativeBind = tableLegalRepresentativeBind;
	}

	/**
	 * Método que configura el logo de la entidad en el momento en que se inicia
	 * la página
	 */
	public void configImegeFile() {
		String path = "";
		java.io.File f = null;
		String fileName = "";

		if (getInstance() != null && getInstance().getId() > 0) {
			if (getInstance() != null && getInstance().getImagePath() != null
					&& !getInstance().getImagePath().isEmpty() && files != null
					&& files.isEmpty()) {
				path = new String(getInstance().getImagePath());
				fileName = path.substring(path.lastIndexOf("/") + 1);
				path = path.substring(0, path.lastIndexOf("/"));
				f = new java.io.File(path + "/" + fileName);
				if (f.exists()) {
					try {
						FileImageInputStream fiis = new FileImageInputStream(f);

						byte[] data = new byte[((fiis.length() == -1) ? 0
								: new Long(fiis.length()).intValue())];
						fiis.read(data);
						Image file = new Image();
						file.setLength(data.length);
						file.setName(f.getName());
						file.setData(data);

						files.add(file);
					} catch (FileNotFoundException e) {
						e.printStackTrace();
						throw new RuntimeException(
								"Problemas con el archivo. ", e);
					} catch (IOException e) {
						e.printStackTrace();
						throw new RuntimeException(
								"Problemas con la escritura del archivo. ", e);
					}
				}
			}
		}
	}

	public int getSize() {
		if (getFiles().size() > 0) {
			return getFiles().size();
		} else {
			return 0;
		}
	}

	public void paint(OutputStream stream, Object object) throws IOException {
		stream.write(getFiles().get((Integer) object).getData());
	}

	/**
	 * Método que se ejecuta en el momento en que se sube el logo del terceo
	 * 
	 * @param event
	 *            evento que lanza este método
	 * @throws Exception
	 */
	public void listener(UploadEvent event) throws Exception {
		UploadItem item = event.getUploadItem();
		this.instance.setLogo(item.getData());
		// File file = new File();
		// file.setLength(item.getData().length);
		// file.setName(item.getFileName());
		// file.setData(item.getData());
		// saveOrUpdateFile(file);
		//
		// files.clear();
		// files.add(file);

		uploadsAvailable--;
	}

	private void saveOrUpdateFile(Image file) throws IOException {
		String path = "";
		java.io.File f = null;
		String extension = "";
		String fileName = "";

		if (!imagePathFullTemp.equals("")) {
			path = new String(imagePathFullTemp);
			fileName = path.substring(path.lastIndexOf("/") + 1);
			path = path.substring(0, path.lastIndexOf("/"));

		} else {
			extension = file.getMime().substring(
					file.getMime().lastIndexOf("/") + 1);
			String server = FacesContext.getCurrentInstance()
					.getExternalContext().getRequestServerName();
			path = FacesContext.getCurrentInstance().getExternalContext()
					.getRealPath(server);
			path = path.substring(0, path.lastIndexOf("/")) + "/";
			path += "img";
			fileName = new Random().nextLong() + "." + extension;

			while (new java.io.File(path + "/" + fileName).exists()) {
				fileName = new Random().nextLong() + "." + extension;
			}
			imagePathFullTemp = path + "/" + fileName;
		}

		f = new java.io.File(path);
		if (!f.exists())
			f.mkdirs();
		f = new java.io.File(path + "/" + fileName);
		FileOutputStream fos = new FileOutputStream(f, false);
		fos.write(file.getData());
		getInstance().setImagePath(path + "/" + fileName);
	}

	public String clearUploadData() {
		deletFile();
		files.clear();
		setUploadsAvailable(1);
		return null;
	}

	private void deletFile() {
		this.getInstance().setLogo(null);
		// String path = "";
		// java.io.File f = null;
		// String fileName = "";
		//
		// if (getInstance() != null && getInstance().getId() > 0) {
		// if (getInstance() != null && getInstance().getImagePath() != null &&
		// !getInstance().getImagePath().isEmpty()) {
		// path = new String(getInstance().getImagePath());
		// fileName = path.substring(path.lastIndexOf("/") + 1);
		// path = path.substring(0, path.lastIndexOf("/"));
		// f = new java.io.File(path + "/" + fileName);
		// if (f.exists()) {
		// f.delete();
		// }
		// }
		// }
	}

	public long getTimeStamp() {
		return System.currentTimeMillis();
	}

	public ArrayList<Image> getFiles() {
		return files;
	}

	public void setFiles(ArrayList<Image> files) {
		this.files = files;
	}

	public int getUploadsAvailable() {
		return uploadsAvailable;
	}

	public void setUploadsAvailable(int uploadsAvailable) {
		this.uploadsAvailable = uploadsAvailable;
	}

	public boolean isAutoUpload() {
		return autoUpload;
	}

	public void setAutoUpload(boolean autoUpload) {
		this.autoUpload = autoUpload;
	}

	public boolean isUseFlash() {
		return useFlash;
	}

	public void setUseFlash(boolean useFlash) {
		this.useFlash = useFlash;
	}

	public byte[] fileData() throws FileNotFoundException, IOException {
		byte[] data = new byte[0];
		String path = "";
		java.io.File f = null;
		String fileName = "";

		if (getInstance() != null && getInstance().getImagePath() != null
				&& !getInstance().getImagePath().isEmpty()) {
			path = new String(getInstance().getImagePath());
			fileName = path.substring(path.lastIndexOf("/") + 1);
			path = path.substring(0, path.lastIndexOf("/"));
			f = new java.io.File(path + "/" + fileName);
			if (f.exists()) {
				FileImageInputStream fiis = new FileImageInputStream(f);

				data = new byte[((fiis.length() == -1) ? 0 : new Long(
						fiis.length()).intValue())];
				fiis.read(data);
			}
		}

		return data;
	}

	/**
	 * Método que representa la cancelación de una edición
	 */
	public void cancelEvent() {
		this.deletePhoneAddress();
		cleanImageTemp();
	}

	/**
	 * Método que limpia la imagen del logo que esta temporalmente
	 */
	private void cleanImageTemp() {
		if (!imagePathFullTemp.equals("")) {
			String fileName = imagePathFullTemp.substring(imagePathFullTemp
					.lastIndexOf("/") + 1);
			String path = imagePathFullTemp.substring(0,
					imagePathFullTemp.lastIndexOf("/"));
			java.io.File f = new java.io.File(path + "/" + fileName);
			if (f.exists()) {
				f.delete();
			}
		}
	}

	public String getIdentificationTypes(BusinessEntity businessEntity) {
		TreeMap<Integer, String> BusinessEntityIdentificationType = new TreeMap<Integer, String>();
		BusinessEntityIdentificationType
				.put(11, "Registro civil de nacimiento");
		BusinessEntityIdentificationType.put(12, "Tarjeta de identidad");
		BusinessEntityIdentificationType.put(13, "Cédula de ciudadanía");
		BusinessEntityIdentificationType.put(14,
				"Certificado Registraduría sin identificación");
		BusinessEntityIdentificationType.put(21, "Tarjeta de extranjería");
		BusinessEntityIdentificationType.put(21, "Cédula de extranjería");
		BusinessEntityIdentificationType.put(31, "NIT");
		BusinessEntityIdentificationType.put(33,
				"Identificación de extranjeros diferente al NIT asignado DIAN");
		BusinessEntityIdentificationType.put(41, "Pasaporte");
		BusinessEntityIdentificationType.put(42,
				"Documento de identificación extranjero");
		BusinessEntityIdentificationType.put(43,
				"Sin identificación del exterior o para uso definido por DIAN");
		BusinessEntityIdentificationType.put(44,
				"Documento de Identificación extranjero Persona Jurídica");
		BusinessEntityIdentificationType.put(90, "Sistema");

		return BusinessEntityIdentificationType.get(new Short(businessEntity
				.getIdType()).intValue());
	}

	public String getLegalEntityType(BusinessEntity businessEntity) {
		TreeMap<String, String> BusinessEntityLegalEntityType = new TreeMap<String, String>();
		BusinessEntityLegalEntityType.put("1", "Persona jurídica");
		BusinessEntityLegalEntityType.put("2",
				"Persona natural o sucesión ilíquida");

		return BusinessEntityLegalEntityType.get(businessEntity
				.getLegalEntityType());
	}

	private static final int[] VALIDATION_VECTOR = { 3, 7, 13, 17, 19, 23, 29,
			37, 41, 43, 47, 53, 59, 67, 71 };

	public void checkDigitCalculation() {
		
		try {
			Long.parseLong(this.instance.getIdNumber());
		}
		catch (NumberFormatException e) {
			e.printStackTrace();
			this.messageIdNumber="El Valor ingresado solo debe contener caracteres numericos ";
			this.instance.setIdNumber("");
			return;
		}
		
		
		
		this.idNumberExist();
		int i;
		long x;
		long y;
		int z;
		long dv1;
		long s;

		x = 0;
		y = 0;

		if (getInstance().idNumber() != null && nitSelected==true) {
			z = getInstance().idNumber().length();

			for (i = 0; i < z; i++) {
				y = Long.parseLong(getInstance().idNumber().substring(i, i + 1));

				s = VALIDATION_VECTOR[z - i - 1];
				x += (y * s);
			}
			y = x % 11;

			if (y > 1) {
				dv1 = 11 - y;
			} else {
				dv1 = y;
			}

			getInstance().setVerificationNumber((new Long(dv1)).intValue());
		}
	}

	public String getMessageIdNumber() {
		return messageIdNumber;
	}

	public void setMessageIdNumber(String messageIdNumber) {
		this.messageIdNumber = messageIdNumber;
	}

	public void idNumberExist() {

		if (this.getBusinessEntityExistingTree() != null) {

			Query q = getEntityManager().createQuery("FROM BusinessEntity be  where be.idNumber=?");
			q.setParameter(1, this.instance.getIdNumber());
			if(q.getResultList().size()!=0){
				this.messageIdNumber = "El Numero de identificación ya existe, por favor ingrese un numero  nuevo";
			}
			else
			{
				this.messageIdNumber = "";
			}
	}
	}

	public boolean checkMessages() {
		if (this.messageIdNumber != null && !this.messageIdNumber.isEmpty()) {
			return true;
		}
		return false;
	}

	public boolean checkIsBiller() {
		if (this.instance.isIsBiller() == true) {
			return true;
		} else {
			return false;
		}
	}

	public TreeMap<String, Object[]> getBusinessEntityExistingTree() {

		if (businessEntityExistingTree == null) {

			Query query = this
					.getEntityManager()
					.createQuery(
							"SELECT tercero.id, tercero.idType, tercero.idNumber FROM BusinessEntity tercero");
			@SuppressWarnings("unchecked")
			ArrayList<Object[]> array = (ArrayList<Object[]>) query
					.getResultList();
			this.businessEntityExistingTree = new TreeMap<String, Object[]>();
			for (Object[] businessEntity : array) {
				String key = businessEntity[1] + "@" + businessEntity[2];
				// System.out.println("################################################  "
				// + key);
				businessEntityExistingTree.put(key, businessEntity);
			}
		}
		return this.businessEntityExistingTree;
	}

	public void setBusinessEntityExistingTree(
			TreeMap<String, Object[]> businessEntityExistingTree) {
		this.businessEntityExistingTree = businessEntityExistingTree;
	}

	/**
	 * el metodo devuelve un string de los tipos de entidad separados por coma
	 * relacionadas con la entidad de negocio
	 * 
	 * @param businessEntity
	 * @return
	 */
	public String convertEntitiesType(BusinessEntity businessEntity) {
		String name = "";
		String coma = "";
		for (BusinessEntityType item : businessEntity.getEntityTypes()) {
			name += coma + item.getNameBusinessEntityType();
			coma = ",  ";
		}
		return name;

	}

	// @In(required=false)
	// private Authenticator authenticator;
	//
	// public List<BusinessEntityType> enTypes = new
	// ArrayList<BusinessEntityType>();
	//
	// public List<BusinessEntityType> EntityTypes()
	// {
	// if(!authenticator.validateShowFunction("BusinessEntityEdit.xhtml",
	// "withRenableUnitType"))
	// {
	// for(BusinessEntityType list :this.instance.getEntityTypes() )
	// {
	//
	//
	// return this.instance.getEntityTypes();
	//
	// }
	// }
	// else
	// {
	// return (this.instance.getEntityTypes());
	// }
	// }

	public BusinessEntity getInstanceMaker(MakerChecker makerChecker) {
		Object aux = new MakerCheckerHome().getInstance(makerChecker);
		if (aux instanceof BusinessEntity) {
			BusinessEntity businessEntity = (BusinessEntity) aux;
			businessEntity = getEntityManager().merge(businessEntity);
			return businessEntity;
		}
		return null;
	}

	private List<PhoneNumber> getPhoneNumbersCache() {
		if (phoneNumbersCache == null) {
			phoneNumbersCache = new ArrayList<PhoneNumber>();
		}
		return phoneNumbersCache;
	}

	private List<Address> getAddressCache() {
		if (addressCache == null) {
			addressCache = new ArrayList<Address>();
		}
		return addressCache;
	}

	public void addPhoneNumber(int phoneNumberId) {
		PhoneNumber p = getEntityManager().find(PhoneNumber.class,
				phoneNumberId);
		boolean aux = false;
		for (PhoneNumber phoneNumber : getInstance().getPhoneNumbers()) {
			if (phoneNumber.getId() == phoneNumberId) {
				aux = true;
			}
		}
		if (!aux) {
			getInstance().getPhoneNumbers().add(p);
			getPhoneNumbersCache().add(p);
		}
		phoneNumberHome.clearInstance();
	}

	public void addAddress(int addressId) {
		Address a = getEntityManager().find(Address.class, addressId);
		boolean aux = false;
		for (Address address : getInstance().getAddresses()) {
			if (address.getId() == addressId) {
				aux = true;
			}
		}
		if (!aux) {
			getInstance().getAddresses().add(a);
			getAddressCache().add(a);
		}
		addressHome.clearInstance();

	}

	@Transactional(TransactionPropagationType.REQUIRED)
	public void deletePhoneAddress() {
		Session session = (Session) getEntityManager().getDelegate();

		for (PhoneNumber phoneNumber : getPhoneNumbersCache()) {

			session.delete(phoneNumber);
			session.flush();
			getInstance().getPhoneNumbers().remove(phoneNumber);
		}

		for (Address address : getAddressCache()) {

			session.delete(address);
			session.flush();
			getInstance().getAddresses().remove(address);
		}

		// for (PhoneNumber phoneNumber : getInstance().getPhoneNumbers()) {
		// phoneNumber.setBusinessEntity(this.instance);
		// getEntityManager().remove(phoneNumber);
		// getEntityManager().flush();
		// }
		// for(Address address : getInstance().getAddresses()){
		// address.setBusinessEntity(this.instance);
		// getEntityManager().remove(address);
		// getEntityManager().flush();
		// }
	}

	@SuppressWarnings("unchecked")
	public List<EconomicActivity> getEconomitActivityList() {
		if (this.economitActivityList == null) {
			Query q = this.getEntityManager().createQuery(
					"from EconomicActivity");
			this.economitActivityList = (List<EconomicActivity>) q
					.getResultList();
			return this.economitActivityList;
		} else {
			return economitActivityList;
		}

	}

	public void setEconomitActivityList(
			List<EconomicActivity> economitActivityList) {
		this.economitActivityList = economitActivityList;
	}

	/**
	 * Metodo que filtra el tipo de la entidad a partir de una lista
	 * 
	 * @param list
	 * @param idType
	 * @return
	 */
	public List<BusinessEntity> filterListForType(List<BusinessEntity> list,
			int idType) {

		// Variable que contiene los daots que se quitrana de la lista original
		// según el tipo de la entidad
		List<BusinessEntity> aux = new ArrayList<BusinessEntity>();

		for (BusinessEntity businessEntity : list) {
			for (BusinessEntityType entityType : businessEntity
					.getEntityTypes()) {
				if (entityType.getId() != idType) {
					aux.add(businessEntity);
					break;
				}
			}
		}

		for (BusinessEntity businessEntity : aux) {
			list.remove(businessEntity);
		}

		return list;
	}

	public List<BusinessEntityType> otherEntityType() {
		List<BusinessEntityType> entityTypes = new BusinessEntityTypeList()
				.getResultList();
		List<BusinessEntityType> otherEntityType = new ArrayList<BusinessEntityType>();

		String query = "SELECT a.* FROM action a, role r, role_features rf, features f "
				+ "WHERE r.id = ? AND f.page = ? AND rf.role= r.id AND rf.features = f.id AND a.features = f.id AND a.action='DisableEntityTypeField'";
		Query q = this.getEntityManager()
				.createNativeQuery(query, Action.class);
		q.setParameter(1, user.getRole().getId());
		q.setParameter(2, "BusinessEntityEdit.xhtml");
		q.setMaxResults(1);
		List<?> l = q.getResultList();

		if (l != null && !l.isEmpty()) {
			for (BusinessEntityType Type : entityTypes) {
				if (!Type.getNameBusinessEntityType().equals(
						"Unidad de Negocio"))
					otherEntityType.add(Type);
			}

			return otherEntityType;
		} else
			return entityTypes;

	}
	
	public void deleteContact(BusinessEntityContact contact)
	{
		Session session = (Session) getEntityManager().getDelegate();

			session.delete(contact);
			session.flush();
			getInstance().getBusinessEntityContacts().remove(contact);
	}

	@Override
	protected void initDefaultMessages() {
		Expressions expressions = new Expressions();
		if (getCreatedMessage() == null) {
			setCreatedMessage(expressions
					.createValueExpression(StatusMessage.getBundleMessage("successfully.created", "Successfully created")));
		}
		if (getUpdatedMessage() == null) {
			setUpdatedMessage(expressions
					.createValueExpression(StatusMessage.getBundleMessage("successfully.updated", "Successfully updated")));
		}
		if (getDeletedMessage() == null) {
			setDeletedMessage(expressions
					.createValueExpression(StatusMessage.getBundleMessage("successfully.deleted", "Successfully deleted")));
		}
	}
}
