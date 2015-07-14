/*=========================================================================
 * Copyright (c) 2010-2014 Pivotal Software, Inc. All rights reserved.
 * This product is protected by U.S. and international copyright
 * and intellectual property laws. Pivotal products are covered by
 * one or more patents listed at http://www.gopivotal.com/patents.
 *=========================================================================
 */
package com.gopivotal.tola.opc.integration.config;

import org.springframework.beans.BeanMetadataElement;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.integration.config.xml.AbstractPollingInboundChannelAdapterParser;
import org.springframework.integration.config.xml.IntegrationNamespaceUtils;
import org.springframework.util.StringUtils;
import org.w3c.dom.Element;

import com.gopivotal.tola.opc.integration.inbound.OpcDaMessageProducer;

public class OpcInboundChannelAdapterParser extends AbstractPollingInboundChannelAdapterParser {
	
	/*
	private String host;
	private String domain;
	private String user;
	private String password;
		 */

	@Override
	protected BeanMetadataElement parseSource(final Element element, final ParserContext parserContext) {
		BeanDefinitionBuilder sourceBuilder = BeanDefinitionBuilder.genericBeanDefinition(OpcDaMessageProducer.class);
		sourceBuilder.addConstructorArgValue(element.getAttribute("url"));
		sourceBuilder.addConstructorArgValue(element.getAttribute(ID_ATTRIBUTE));
		String feedFetcherRef = element.getAttribute("feed-fetcher");
		if (StringUtils.hasText(feedFetcherRef)) {
			sourceBuilder.addConstructorArgReference(feedFetcherRef);
		}
		IntegrationNamespaceUtils.setReferenceIfAttributeDefined(sourceBuilder, element, "metadata-store");

		return sourceBuilder.getBeanDefinition();
	}


}
