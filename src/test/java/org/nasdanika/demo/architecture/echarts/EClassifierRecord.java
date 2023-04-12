package org.nasdanika.demo.architecture.echarts;

import java.util.Collection;

import org.json.JSONObject;

public record EClassifierRecord(JSONObject node, Collection<JSONObject> links) {

}
