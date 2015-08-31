package com.github.kaiwinter.rhapsody.model;

/**
 * Data structure which gets filled with the result of a REST call to the Rhapsody API.
 */
public final class AccountData {
	public String cobrand;
	public String cocat;
	public String email;
	public String logon;
	public String firstName;
	public String id;
	public String lastName;
	public String locale;
	public String country;
	public Boolean isPublic;
	public String billingPartnerCode;
	public String catalog;
	public Long createDate;
	public Boolean isSuspended;
	public String tierCode;
	public String tierName;
	public String productCode;
	public String productName;
	public Integer expirationDate;
	public Integer trialLengthDays;
	public Boolean isTrial;
	public String state;
	public Boolean canStreamOnWeb;
	public Boolean canStreamOnMobile;
	public Boolean canStreamOnHomeDevice;
	public Boolean canStreamOnPC;
	public Boolean canUpgradeStreams;
	public Boolean canPlayPremiumRadio;
	public Integer maxStreamCount;
	public Boolean isPlayBasedTier;
	public Boolean isMonthlyPlayBasedTier;
	public Boolean isOneTimePlayBasedTier;
	public Object totalPlays;
	public Object playsRemaining;
	public Object skipLimit;
	public Object skipLimitMinutes;
}
