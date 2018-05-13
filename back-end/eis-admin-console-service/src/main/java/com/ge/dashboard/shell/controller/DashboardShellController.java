package com.ge.dashboard.shell.controller;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.ge.dashboard.shell.model.UrlInfo;

@RestController
@RequestMapping("dashboardShell")
public class DashboardShellController {

	@Autowired
	private Environment env;

	@RequestMapping("/urlInfos")
	public List<UrlInfo> getUrlInfos() {

		List<UrlInfo> urlsInfos = new ArrayList<UrlInfo>();

		UrlInfo identityUrlInfo = new UrlInfo();
		identityUrlInfo.setId(Utils.IDENTITY_URL);
		identityUrlInfo.setUrl(env.getProperty(Utils.IDENTITY_URL, "#"));

		UrlInfo k8sUrlInfo = new UrlInfo();
		k8sUrlInfo.setId(Utils.K8S_URL);
		k8sUrlInfo.setUrl(env.getProperty(Utils.K8S_URL, "#"));

		UrlInfo telemeteryUrlInfo = new UrlInfo();
		telemeteryUrlInfo.setId(Utils.TELEMETERY_URL);
		telemeteryUrlInfo.setUrl(env.getProperty(Utils.TELEMETERY_URL, "#"));

		UrlInfo titaniumUrlInfo = new UrlInfo();
		titaniumUrlInfo.setId(Utils.TITANIUM_URL);
		titaniumUrlInfo.setUrl(env.getProperty(Utils.TITANIUM_URL, "#"));

		UrlInfo grafanaUrlInfo = new UrlInfo();
		grafanaUrlInfo.setId(Utils.GRAFANA_URL);
		grafanaUrlInfo.setUrl(env.getProperty(Utils.GRAFANA_URL, "#"));

		UrlInfo kibanaUrlInfo = new UrlInfo();
		kibanaUrlInfo.setId(Utils.KIBANA_URL);
		kibanaUrlInfo.setUrl(env.getProperty(Utils.KIBANA_URL, "#"));

		UrlInfo prometheusUrlInfo = new UrlInfo();
		prometheusUrlInfo.setId(Utils.PROMETHEUS_URL);
		prometheusUrlInfo.setUrl(env.getProperty(Utils.PROMETHEUS_URL, "#"));

		UrlInfo deveiceRegistryUrlInfo = new UrlInfo();
		deveiceRegistryUrlInfo.setId(Utils.DEVICEREGISTRY_URL);
		deveiceRegistryUrlInfo.setUrl(env.getProperty(Utils.DEVICEREGISTRY_URL, "#"));
		
		UrlInfo dicomdataListingInfo = new UrlInfo();
		dicomdataListingInfo.setId(Utils.DICOMDATALISTING_URL);
		dicomdataListingInfo.setUrl(env.getProperty(Utils.DICOMDATALISTING_URL, "#"));

		urlsInfos.add(k8sUrlInfo);
		urlsInfos.add(telemeteryUrlInfo);
		urlsInfos.add(titaniumUrlInfo);
		urlsInfos.add(identityUrlInfo);
		urlsInfos.add(grafanaUrlInfo);
		urlsInfos.add(kibanaUrlInfo);
		urlsInfos.add(prometheusUrlInfo);
		urlsInfos.add(deveiceRegistryUrlInfo);
		urlsInfos.add(dicomdataListingInfo);

		return urlsInfos;
	}

}
