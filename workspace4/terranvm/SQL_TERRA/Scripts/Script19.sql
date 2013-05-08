
------------------------Script19 add 02/11/2011-----------------------------------

----------------12-01-2012---------------WAMAYA--------------------------------------
delete from sales_period sp where not id in (select sales_period from sales where sales_period = sp.id);
ALTER TABLE concept add column last_modification date;
