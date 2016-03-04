package TestCases;

import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;

import java.util.HashMap;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Unmarshaller;

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

public class TestUnitForReadByCalimNumber {

	private HashMap<String, MitchellClaimType> references;
	private final String DATABASE_URL = "jdbc:sqlite:mem:testCreate";
	
	
	@BeforeClass
	public static void setUp(){
		System.setProperty(LocalLog.LOCAL_LOG_LEVEL_PROPERTY, "ERROR");
	}
	
	
	@Before
	public void intilizeDateBase(){
		ConnectionSource connectionSource = null;	
		/**
		 *  create 100 random MitchellClaim XML String to test createMitchellClaim API
		 */
		references = new HashMap<String, MitchellClaimType>();
		String[] testXMLStrings = TestXMLGenerator.generateTestXMLString(100);
		MitchellClaimService mcService = new MitchellClaimService(DATABASE_URL);
		mcService.setUp();
		
		try{

			/**
			 *  initialize data base
			 */
			connectionSource = new JdbcConnectionSource(DATABASE_URL);
			
			TableUtils.dropTable(connectionSource, MitchellClaimBasicInfo.class, true);
			TableUtils.createTableIfNotExists(connectionSource, MitchellClaimBasicInfo.class);
			TableUtils.dropTable(connectionSource, MitchellClaimLossInfo.class, true);
			TableUtils.createTableIfNotExists(connectionSource, MitchellClaimLossInfo.class);
			TableUtils.dropTable(connectionSource, MitchellClaimVehicleInfo.class, true);
			TableUtils.createTableIfNotExists(connectionSource, MitchellClaimVehicleInfo.class);
			
			for (String XMLString : testXMLStrings){
				
				/**
				 *  create reference MitchellClaimType instance
				 */
				JAXBContext context = JAXBContext.newInstance(ObjectFactory.class);
	            Unmarshaller un = context.createUnmarshaller();
	            MitchellClaimType mct = ((JAXBElement<MitchellClaimType>) un.unmarshal(new ByteArrayInputStream(XMLString.getBytes(StandardCharsets.UTF_8)))).getValue();
				references.put(mct.getClaimNumber(), mct);
	            
	            /**
	             * use createMitchellClaim API
	             */
	            mcService.createMitchellClaim(XMLString);
	            
			}
			
			mcService.tearDown();
		}catch (Exception e){
			e.printStackTrace();
		}finally{
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
	public void testWithInvalidInput(){
		MitchellClaimService mcs = new MitchellClaimService(DATABASE_URL);
		mcs.setUp();
		
		assertTrue(mcs.readByClaimNumber(null) == null);
		
		mcs.tearDown();
	}
	
	@Test
	public void testWithExistingClaimNumber(){		
		/**
		 *  initialize ORMlite DAO for verifying DB status
		 */
				
		MitchellClaimService mcs = new MitchellClaimService(DATABASE_URL);
		mcs.setUp();
		
		for (String key : references.keySet()){
				
			assertTrue(mcs.readByClaimNumber(key).equals(references.get(key)));
	            
		}
		mcs.tearDown();
	}
	
	
	@Test 
	public void TestWithNonExistingClaimNumber(){
		int numberOfTries = 100;
		
		MitchellClaimService mcs = new MitchellClaimService(DATABASE_URL);
		mcs.setUp();
		
		for (int i = 0; i < numberOfTries; ++i){
			String cn = TestXMLGenerator.randomSequenceGenerator(32, 1, false)[0];
			if (!references.containsKey(cn)){
				assertTrue(mcs.readByClaimNumber(cn) == null);
			}
		}
		
		mcs.tearDown();
	}
	
	@AfterClass
	public static void tearDown(){
		
	}
}
