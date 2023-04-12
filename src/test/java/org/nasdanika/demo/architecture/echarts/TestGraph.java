package org.nasdanika.demo.architecture.echarts;


import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.eclipse.emf.ecore.EPackage;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.nasdanika.architecture.cloud.azure.storage.StoragePackage;
import org.nasdanika.architecture.containers.docker.DockerPackage;
import org.nasdanika.architecture.containers.helm.HelmPackage;
import org.nasdanika.architecture.containers.kubernetes.KubernetesPackage;
import org.nasdanika.architecture.c4.C4Package;
import org.nasdanika.architecture.cloud.azure.compute.ComputePackage;
import org.nasdanika.architecture.cloud.azure.networking.NetworkingPackage;
import org.nasdanika.common.Context;
import org.nasdanika.common.NullProgressMonitor;
import org.nasdanika.common.ProgressMonitor;
import org.nasdanika.exec.ExecPackage;
import org.nasdanika.graph.emf.EObjectNode;
import org.nasdanika.graph.emf.Util;
import org.nasdanika.graph.processor.ProcessorInfo;
import org.nasdanika.html.model.app.AppPackage;
import org.nasdanika.html.model.bootstrap.BootstrapPackage;
import org.nasdanika.html.model.html.HtmlPackage;


public class TestGraph {
	
	private void generateGraph(String name, List<EPackage> ePackages) throws IOException {
		List<EObjectNode> nodes = Util.load(ePackages.stream().filter(ep -> ep.getESuperPackage() == null).collect(Collectors.toList()));
		
		GraphProcessorFactory processorFactory = new GraphProcessorFactory(ePackages);
		ProgressMonitor progressMonitor = new NullProgressMonitor(); // new PrintStreamProgressMonitor();
		Registry registry = processorFactory.createProcessors(nodes, progressMonitor);
		
		List<JSONObject> jNodes = new ArrayList<>();
		List<JSONObject> jLinks = new ArrayList<>();		
		
		registry
			.infoMap()
			.values()
			.stream()
			.map(ProcessorInfo::getProcessor)			
			.filter(Objects::nonNull)
			.map(Supplier::get)
			.forEach(r -> {
				jNodes.add(r.node());
				jLinks.addAll(r.links());
			});
		
		JSONObject result = new JSONObject();
		result.put("nodes", jNodes);
		result.put("links", jLinks);
		
		JSONArray jCategories = new JSONArray();
		for (EPackage ePackage: ePackages) {
			JSONObject jPackage = new JSONObject();
			jPackage.put("name", ePackage.getName());
			jCategories.put(jPackage);
		}
		result.put("categories", jCategories);
		
		String pageTemplate = Files.readString(new File("graph-template.html").toPath());	
		Context context = Context.singleton("graph", result.toString(4)).compose(Context.singleton("title", name));
		String html = context.interpolateToString(pageTemplate);
		Files.writeString(new File("docs\\" + name + ".html").toPath(), html, StandardCharsets.UTF_8);
		
	}
		
	@Test
	public void testGraph() throws Exception {
		generateGraph("HTML", List.of(HtmlPackage.eINSTANCE, BootstrapPackage.eINSTANCE, AppPackage.eINSTANCE));
		generateGraph("Azure", List.of(org.nasdanika.architecture.cloud.azure.core.CorePackage.eINSTANCE, ComputePackage.eINSTANCE, NetworkingPackage.eINSTANCE, StoragePackage.eINSTANCE));
		
		List<EPackage> allEPackages = Arrays.asList(
				org.nasdanika.ncore.NcorePackage.eINSTANCE,
				ExecPackage.eINSTANCE,
				org.nasdanika.exec.content.ContentPackage.eINSTANCE,
				org.nasdanika.exec.resources.ResourcesPackage.eINSTANCE,
				HtmlPackage.eINSTANCE, 
				BootstrapPackage.eINSTANCE, 
				AppPackage.eINSTANCE,
				org.nasdanika.architecture.core.CorePackage.eINSTANCE,
				C4Package.eINSTANCE,
				org.nasdanika.architecture.cloud.azure.core.CorePackage.eINSTANCE, 
				ComputePackage.eINSTANCE, 
				NetworkingPackage.eINSTANCE, 
				StoragePackage.eINSTANCE,
				DockerPackage.eINSTANCE,
				KubernetesPackage.eINSTANCE,
				HelmPackage.eINSTANCE
				);
		generateGraph("all", allEPackages);
		
	}
	
}
