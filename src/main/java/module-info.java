module org.nasdanika.demo.architecture.echarts {
		
	requires transitive org.nasdanika.html.model.app;
	
	requires org.nasdanika.architecture.c4;
	
	requires org.nasdanika.architecture.cloud.azure.compute;
	requires org.nasdanika.architecture.cloud.azure.networking;
	requires org.nasdanika.architecture.cloud.azure.storage;
	
	requires org.nasdanika.architecture.containers.docker;
	requires org.nasdanika.architecture.containers.kubernetes;
	requires org.nasdanika.architecture.containers.helm;
	
}
