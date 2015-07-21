/*=========================================================================
 * Copyright (c) 2010-2014 Pivotal Software, Inc. All rights reserved.
 * This product is protected by U.S. and international copyright
 * and intellectual property laws. Pivotal products are covered by
 * one or more patents listed at http://www.gopivotal.com/patents.
 *=========================================================================
 */
package com.gopivotal.tola.opc.type;

/**
 * Quality - Represents an Item Quality
 * 
 * QQ BIT VALUE DEFINE DESCRIPTION 
 * 0 00SSSSLL Bad Value is not useful for reasons indicated by the Substatus. 
 * 1 01SSSSLL Uncertain The quality ofthe value is uncertain for reasons indicated by the Substatus. 
 * 2 10SSSSLL N/A Not used by OPC 
 * 3 11SSSSLL Good The Quality of the value is Good.
 * 
 * @author mborges
 *
 */
public class Quality {
	
	// Quality
	public final short QQ_BAD = 0x0000;
	public final short QQ_UNCERTAIN = 0x0040;
	public final short QQ_NA = 0x0080;
	public final short QQ_GOOD = 0x00C0;
	
	// SSSS - substatus
	public final short BAD_NO_SPECIFIC = 0x0000;
	public final short BAD_CONFIGURATION_ERROR = 0x0004;
	public final short BAD_NOT_CONNECTED = 0x0008;
	public final short BAD_DEVICE_FAILURE = 0x000C;
	public final short BAD_SENSOR_FAILURE = 0x0010;
	public final short BAD_LAST_KNOWN_VALUE = 0x0014;
	public final short BAD_COMM_FAILURE = 0x0018;
	public final short BAD_OUT_OF_SERVICE = 0x001C;
	
	public final short UNCERTAIN_NON_SPECIFIC = 0x0040;
	public final short UNCERTAIN_LAST_USABLE_VALUE = 0x0044;
	public final short UNCERTAIN_SENSOR_NOT_ACCURATE = 0x0050;
	public final short UNCERTAIN_EU_UNITS_EXCEEDED = 0x0054;
	public final short UNCERTAIN_SUB_NORMAL = 0x0058;

	public final short GOOD_NON_SPECIFIC = 0x00C0;
	public final short GOOD_LOCAL_OVERRIDE = 0x00D8;
	
	// LL - Limit
	public final short LL_FREE = 0x0000;
	public final short LL_LOW = 0x0001;
	public final short LL_HIGH = 0x002;
	public final short LL_CONSTANT = 0x0003;
	
	private final short value;
	
	// constructor
	public Quality(short value) {
		this.value = value;
	}
	
	public static String format(short value) {
		return new Quality(value).quality();
	}
	
	public String quality() {
		int qq = value & 0x00C0;
		switch(qq) {
		case QQ_GOOD: return "good";
		case QQ_UNCERTAIN: return "uncertain";
		case QQ_BAD: return "bad";
		default: return "n/a";
		}
	}
	
	public String substatus() {
		
		int ssss = value & 0x00FC;
		
		switch(ssss) {
		case BAD_NO_SPECIFIC: return "bad non-specific";
		case BAD_CONFIGURATION_ERROR: return "bad configuration error";
		case BAD_NOT_CONNECTED: return "bad not connected";
		case BAD_DEVICE_FAILURE: return "bad device failure";
		case BAD_SENSOR_FAILURE: return "bad sensor failure";
		case BAD_LAST_KNOWN_VALUE: return "bad last known value";
		case BAD_COMM_FAILURE: return "bad comm failure";
		case BAD_OUT_OF_SERVICE: return "bad out of service";			
		case UNCERTAIN_NON_SPECIFIC: return "uncertain non specific";
		case UNCERTAIN_LAST_USABLE_VALUE: return "uncertain last usable value";
		case UNCERTAIN_SENSOR_NOT_ACCURATE: return "uncertain sensor not accurate";
		case UNCERTAIN_EU_UNITS_EXCEEDED: return "uncertain eu units exceeded";
		case UNCERTAIN_SUB_NORMAL: return "uncertain sub normal";
		case GOOD_NON_SPECIFIC: return "good non specific";
		case GOOD_LOCAL_OVERRIDE: return "good local override";
		default: return "n/a";
		}
			
	}
	
	public String limit() {
		
		int ll = value & 0x0003;
	
		switch(ll) {
			case LL_FREE: return "free";
			case LL_LOW: return "low";
			case LL_HIGH: return "high";
			case LL_CONSTANT: return "constant";
			default: return "n/a";
		}	
		
	}
	
	
	
	@Override
	public String toString() {
		return "Quality [value=" + value + " Substatus=" + substatus() + " Limit=" + limit() + "]";
	}	

}
