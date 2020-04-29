package ca.magex.crm.hazelcast;

import java.util.Arrays;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import com.hazelcast.core.HazelcastInstance;

import ca.magex.crm.api.lookup.BusinessClassification;
import ca.magex.crm.api.lookup.BusinessSector;
import ca.magex.crm.api.lookup.BusinessUnit;
import ca.magex.crm.api.lookup.Country;
import ca.magex.crm.api.lookup.Language;
import ca.magex.crm.api.lookup.Salutation;
import ca.magex.crm.api.services.CrmLocationService;
import ca.magex.crm.api.services.CrmLookupService;
import ca.magex.crm.api.services.CrmOrganizationService;
import ca.magex.crm.api.services.CrmPersonService;
import ca.magex.crm.api.services.CrmUserService;
import ca.magex.crm.api.system.Role;
import ca.magex.crm.api.system.Status;
import ca.magex.crm.resource.CrmDataInitializer;
import ca.magex.crm.resource.CrmLookupLoader;

@Component
public class CrmHazelcastContextListener implements ApplicationListener<ContextRefreshedEvent> {

	private static final Logger LOG = LoggerFactory.getLogger(CrmHazelcastContextListener.class);
	
	@Autowired private CrmOrganizationService crmOrganizationService;
	@Autowired private CrmLocationService crmLocationService;
	@Autowired private CrmPersonService crmPersonService;
	@Autowired private CrmUserService crmUserService;
	@Autowired private CrmLookupService crmlookupService;
	@Autowired private HazelcastInstance hzInstance;	
	@Autowired private CrmLookupLoader lookupLoader;
	
	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {
		Map<String, Object> initMap = hzInstance.getMap("init");
		if (initMap.containsKey("timestamp")) {
			Long initTimeStamp = (Long) initMap.get("timestamp");
			for (int i=0; i<10; i++) {				
				if (initTimeStamp == 0L) {
					LOG.info("Waiting for hazelcast to be initialized...");
					try {
						Thread.sleep(TimeUnit.SECONDS.toMillis(3));
					}
					catch(InterruptedException ie) {
						if (Thread.currentThread().isInterrupted()) {
							return;
						}
					}
					initTimeStamp = (Long) initMap.get("timestamp");
				}
				else {
					break;
				}
			}
			LOG.info("Hazelcast CRM Previously Initialized on: " + new Date(initTimeStamp));
			return;
		}			
		
		LOG.info("Initializing Hazelcast CRM");
		LOG.info("Initializing Lookups");
		hzInstance.getList("statuses").addAll(Arrays.asList(Status.values()));
		hzInstance.getList("roles").addAll(lookupLoader.loadLookup(Role.class, "Role.csv"));
		hzInstance.getList("countries").addAll(lookupLoader.loadLookup(Country.class, "Country.csv"));
		hzInstance.getList("languages").addAll(lookupLoader.loadLookup(Language.class, "Language.csv"));
		hzInstance.getList("salutations").addAll(lookupLoader.loadLookup(Salutation.class, "Salutation.csv"));
		hzInstance.getList("sectors").addAll(lookupLoader.loadLookup(BusinessSector.class, "BusinessSector.csv"));
		hzInstance.getList("units").addAll(lookupLoader.loadLookup(BusinessUnit.class, "BusinessUnit.csv"));
		hzInstance.getList("classifications").addAll(lookupLoader.loadLookup(BusinessClassification.class, "BusinessClassification.csv"));
		
		/* initialize org data */
		CrmDataInitializer.initialize(crmOrganizationService, crmLocationService, crmPersonService, crmUserService, crmlookupService);
	}		
}