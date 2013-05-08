package org.koghi.terranvm.async;

public class GarbageCollector implements Runnable {
	
	Runtime runtime;
	
	public GarbageCollector(Runtime runtime) {
		this.runtime = runtime;
	}
	
	
	public void run(){
		try {
			System.out.println("El hilo se dormira por 10 segundos mientras se eliminan las referencias a los objetos.");
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
//		System.out.println();
//		System.out.println("GARBAGE COLLECTOR.........");
//		System.out.println("FREE MEMORY: "+(this.runtime.freeMemory()/1024)/1024);
//		System.out.println("TOTAL MEMORY: "+(this.runtime.totalMemory()/1024)/1024);
		this.runtime.runFinalization();
		this.runtime.gc();
//		System.out.println("despues de....");
//		System.out.println("FREE MEMORY: "+(this.runtime.freeMemory()/1024)/1024);
//		System.out.println("TOTAL MEMORY: "+(this.runtime.totalMemory()/1024)/1024);
		
	}

}
