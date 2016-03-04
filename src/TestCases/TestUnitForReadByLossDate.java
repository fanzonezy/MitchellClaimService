package TestCases;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import javax.print.attribute.standard.ReferenceUriSchemesSupported;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Unmarshaller;
import javax.xml.datatype.XMLGregorianCalendar;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.logger.LocalLog;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import MitchellClaimServiceUtils.*;
import MitchellClaimService.*;
import MitchellClaimORMModels.*;

public class TestUnitForReadByLossDate {
	private HashMap<String, MitchellClaimType> references = null;
	private final String DATABASE_URL = "jdbc:sqlite:mem:testCreate";
		
	@BeforeClass
	public static void setUp(){
		System.setProperty(LocalLog.LOCAL_LOG_LEVEL_PROPERTY, "ERROR");
	}
	
	
	public void prepare(Date startDate, Date endDate, int N){
		ConnectionSource connectionSource = null;	
		/**
		 *  create N random MitchellClaim XML String to test createMitchellClaim API
		 */
		String[] testXMLStrings = TestXMLGenerator.generateTestXMLString(N);
		references = new HashMap<String, MitchellClaimType>();
		
		MitchellClaimService mcService = new MitchellClaimService(DATABASE_URL);
		mcService.setUp();
			
		try{

			/**
			 *  initialize ORMlite DAO for verifying DB status
			 */
			connectionSource = new JdbcConnectionSource(DATABASE_URL);
			
			TableUtils.dropTable(connectionSource, MitchellClaimBasicInfo.class, true);
			TableUtils.createTableIfNotExists(connectionSource, MitchellClaimBasicInfo.class);
			TableUtils.dropTable(connectionSource, MitchellClaimLossInfo.class, true);
			TableUtils.createTableIfNotExists(connectionSource, MitchellClaimLossInfo.class);
			TableUtils.dropTable(connectionSource, MitchellClaimVehicleInfo.class, true);
			TableUtils.createTableIfNotExists(connectionSource, MitchellClaimVehicleInfo.class);
			
			/**
             * use createMitchellClaim API
             */
			mcService.createMitchellClaimN(testXMLStrings);
			
			JAXBContext context = JAXBContext.newInstance(ObjectFactory.class);
            Unmarshaller un = context.createUnmarshaller();
			for (String XMLString : testXMLStrings){
				
				/**
				 *  create reference MitchellClaimType instance
				 */
	            MitchellClaimType mct = ((JAXBElement<MitchellClaimType>) un.unmarshal(new ByteArrayInputStream(XMLString.getBytes(StandardCharsets.UTF_8)))).getValue();
				
	            Date lossDate = mct.getLossDate().toGregorianCalendar().getTime();
    
	            if ( lossDate.compareTo(startDate) > 0 && lossDate.compareTo(endDate) < 0 || 
	            	 lossDate.equals(startDate) || lossDate.equals(endDate)){
	            	 references.put(mct.getClaimNumber(), mct);
	            }
    
			}
			
		}catch (Exception e){
			e.printStackTrace();
		}finally{
			mcService.tearDown();
			try{
				if (connectionSource != null){
					connectionSource.close();
				}
			}catch (Exception e){
				e.printStackTrace();
			}
		}
	}

	
	@Test
	public void testWithValidDate(){		

		GregorianCalendar gcStart = new GregorianCalendar(2005, 0, 1);
		GregorianCalendar gcEnd = new GregorianCalendar(2010, 0, 1);
		
		prepare(gcStart.getTime(), gcEnd.getTime(), 100);
		
		MitchellClaimService mcs = new MitchellClaimService(DATABASE_URL);
		mcs.setUp();
		
		List<MitchellClaimType> l_mc = mcs.readByLossDate(gcStart.getTime(), gcEnd.getTime());
		assertEquals(references.size(), l_mc.size());

		for (MitchellClaimType mc : l_mc){
			assertTrue(references.get(mc.getClaimNumber()).equals(mc));
		}
		
		mcs.tearDown();
		
	}
	
	@Test
	public void testWithInvalidDate(){		

		GregorianCalendar gcStart = new GregorianCalendar(2010, 0, 1);
		GregorianCalendar gcEnd = new GregorianCalendar(2005, 0, 1);
		
		prepare(gcStart.getTime(), gcEnd.getTime(), 100);
		
		MitchellClaimService mcs = new MitchellClaimService(DATABASE_URL);
		mcs.setUp();
		
		List<MitchellClaimType> l_mc = mcs.readByLossDate(gcStart.getTime(), gcEnd.getTime());
		assertEquals(0, l_mc.size());

		for (MitchellClaimType mc : l_mc){
			assertTrue(references.get(mc.getClaimNumber()).equals(mc));
		}
		
		mcs.tearDown();
		
	}
		
	@AfterClass
	public static void tearDown(){
		
	}
}
