package ca.magex.crm.amnesia.services;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import org.springframework.stereotype.Service;

import ca.magex.crm.amnesia.Lookups;
import ca.magex.crm.api.exceptions.ItemNotFoundException;
import ca.magex.crm.api.lookup.BusinessClassification;
import ca.magex.crm.api.lookup.BusinessSector;
import ca.magex.crm.api.lookup.BusinessUnit;
import ca.magex.crm.api.lookup.Country;
import ca.magex.crm.api.lookup.Language;
import ca.magex.crm.api.lookup.Salutation;
import ca.magex.crm.api.services.CrmLookupService;
import ca.magex.crm.api.system.Role;
import ca.magex.crm.api.system.Status;

@Service("amnesiaLookupService")
public class AmnesiaLookupService implements CrmLookupService {

	private Lookups<Status, String> statuses;
	
	private Lookups<Role, String> roles;
	
	private Lookups<Country, String> countries;
	
	private Lookups<Salutation, Integer> salutations;
	
	private Lookups<Language, String> languages;
	
	private Lookups<BusinessSector, Integer> sectors;
	
	private Lookups<BusinessUnit, Integer> units;
	
	private Lookups<BusinessClassification, Integer> classifications;
	
	public AmnesiaLookupService() {
		statuses = new Lookups<Status, String>(Arrays.asList(Status.values()), Status.class, String.class);
		roles = new Lookups<Role, String>(Role.class, String.class);
		countries = new Lookups<Country, String>(Country.class, String.class);
		salutations = new Lookups<Salutation, Integer>(Salutation.class, Integer.class);
		languages = new Lookups<Language, String>(Language.class, String.class);
		sectors = new Lookups<BusinessSector, Integer>(BusinessSector.class, Integer.class);
		units = new Lookups<BusinessUnit, Integer>(BusinessUnit.class, Integer.class);
		classifications = new Lookups<BusinessClassification, Integer>(BusinessClassification.class, Integer.class);
	}
	
	public List<Status> findStatuses() {
		return statuses.getOptions();
	}
	
	public Status findStatusByCode(String code) throws ItemNotFoundException {
		return statuses.findByCode(code);
	}
	
	public Status findStatusByLocalizedName(Locale locale, String name) throws ItemNotFoundException {
		return statuses.findByName(locale, name);
	}

	public List<Role> findRoles() {
		return roles.getOptions();
	}
	
	public Role findRoleByCode(String code) throws ItemNotFoundException {
		return roles.findByCode(code);
	}
	
	public Role findRoleByLocalizedName(Locale locale, String name) throws ItemNotFoundException {
		return roles.findByName(locale, name);
	}

	public List<Country> findCountries() {
		return countries.getOptions();
	}
	
	public Country findCountryByCode(String code) throws ItemNotFoundException {
		return countries.findByCode(code);
	}
	
	public Country findCountryByLocalizedName(Locale locale, String name) throws ItemNotFoundException {
		return countries.findByName(locale, name);
	}

	public List<Salutation> findSalutations() {
		return salutations.getOptions();
	}
	
	public Salutation findSalutationByCode(Integer code) throws ItemNotFoundException {
		return salutations.findByCode(code);
	}
	
	public Salutation findSalutationByLocalizedName(Locale locale, String name) throws ItemNotFoundException {
		return salutations.findByName(locale, name);
	}

	@Override
	public List<Language> findLanguages() {
		return languages.getOptions();
	}

	@Override
	public Language findLanguageByCode(String code) throws ItemNotFoundException {
		return languages.findByCode(code);
	}

	@Override
	public Language findLanguageByLocalizedName(Locale locale, String name) throws ItemNotFoundException {
		return languages.findByName(locale, name);
	}

	@Override
	public List<BusinessSector> findBusinessSectors() {
		return sectors.getOptions();
	}

	@Override
	public BusinessSector findBusinessSectorByCode(Integer code) throws ItemNotFoundException {
		return sectors.findByCode(code);
	}

	@Override
	public BusinessSector findBusinessSectorByLocalizedName(Locale locale, String name) throws ItemNotFoundException {
		return sectors.findByName(locale, name);
	}

	@Override
	public List<BusinessUnit> findBusinessUnits() {
		return units.getOptions();
	}

	@Override
	public BusinessUnit findBusinessUnitByCode(Integer code) throws ItemNotFoundException {
		return units.findByCode(code);
	}

	@Override
	public BusinessUnit findBusinessUnitByLocalizedName(Locale locale, String name) throws ItemNotFoundException {
		return units.findByName(locale, name);
	}

	@Override
	public List<BusinessClassification> findBusinessClassifications() {
		return classifications.getOptions();
	}

	@Override
	public BusinessClassification findBusinessClassificationByCode(Integer code) throws ItemNotFoundException {
		return classifications.findByCode(code);
	}

	@Override
	public BusinessClassification findBusinessClassificationByLocalizedName(Locale locale, String name)
			throws ItemNotFoundException {
		return classifications.findByName(locale, name);
	}

}