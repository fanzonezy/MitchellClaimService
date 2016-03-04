package TestCases;


import static org.junit.Assert.assertTrue;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

import java.util.Set;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.logger.LocalLog;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import MitchellClaimServiceUtils.*;
import MitchellClaimService.*;
import MitchellClaimORMModels.*;

public class TestUnitForUpdate {
	@BeforeClass
	public static void initializeDataBase() {
		System.setProperty(LocalLog.LOCAL_LOG_LEVEL_PROPERTY, "ERROR");
	}

	public HashMap<String, MitchellClaimType> prepare(int nTuples) {
		ConnectionSource connectionSource = null;
		HashMap<String, MitchellClaimType> reference = new HashMap<String, MitchellClaimType>();

		/**
		 * create n random MitchellClaim XML String
		 */

		String[] testXMLStrings = TestXMLGenerator.generateTestXMLString(nTuples);
		MitchellClaimService mcService = new MitchellClaimService(DATABASE_URL);
		mcService.setUp();

		try {

			/**
			 * initialize ORMlite DAO for verifying DB status
			 */
			connectionSource = new JdbcConnectionSource(DATABASE_URL);

			TableUtils.dropTable(connectionSource, MitchellClaimBasicInfo.class, true);
			TableUtils.createTableIfNotExists(connectionSource, MitchellClaimBasicInfo.class);
			TableUtils.dropTable(connectionSource, MitchellClaimLossInfo.class, true);
			TableUtils.createTableIfNotExists(connectionSource, MitchellClaimLossInfo.class);
			TableUtils.dropTable(connectionSource, MitchellClaimVehicleInfo.class, true);
			TableUtils.createTableIfNotExists(connectionSource, MitchellClaimVehicleInfo.class);

			for (String XMLString : testXMLStrings) {

				/**
				 * create reference MitchellClaimType instance
				 */
				JAXBContext context = JAXBContext.newInstance(ObjectFactory.class);
				Unmarshaller un = context.createUnmarshaller();
				MitchellClaimType mct = ((JAXBElement<MitchellClaimType>) un
						.unmarshal(new ByteArrayInputStream(XMLString.getBytes(StandardCharsets.UTF_8)))).getValue();

				/**
				 * use createMitchellClaim API
				 */
				mcService.createMitchellClaim(XMLString);

				/**
				 * add into reference
				 */
				reference.put(mct.getClaimNumber(), mct);

			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			mcService.tearDown();
			try {
				if (connectionSource != null) {
					connectionSource.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return reference;

	}

	public static HashMap<String, MitchellClaimType> prepareUpdateXMLStrings(Set<String> claimNumbers) {

		HashMap<String, MitchellClaimType> reference = new HashMap<String, MitchellClaimType>();
		try {
			JAXBContext context = JAXBContext.newInstance(ObjectFactory.class);
			Unmarshaller un = context.createUnmarshaller();

			/** for each claim number **/
			for (String cn : claimNumbers) {
				/** generate a random update XML **/
				String str = TestXMLGenerator.generateTestPartialXMLString(1)[0];
				/** convert to MitchellClaimType **/
				MitchellClaimType mct = ((JAXBElement<MitchellClaimType>) un
						.unmarshal(new ByteArrayInputStream(str.getBytes(StandardCharsets.UTF_8)))).getValue();
				/** set claim number **/
				mct.setClaimNumber(cn);
				/** add to reference **/
				reference.put(cn, mct);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return reference;
	}

	@Test
	public void testWithSample() {
		ConnectionSource connectionSource = null;

		MitchellClaimService mcService = new MitchellClaimService(DATABASE_URL);
		mcService.setUp();

		try {

			/**
			 * initialize ORMlite DAO for verifying DB status
			 */
			connectionSource = new JdbcConnectionSource(DATABASE_URL);

			TableUtils.dropTable(connectionSource, MitchellClaimBasicInfo.class, true);
			TableUtils.createTableIfNotExists(connectionSource, MitchellClaimBasicInfo.class);
			TableUtils.dropTable(connectionSource, MitchellClaimLossInfo.class, true);
			TableUtils.createTableIfNotExists(connectionSource, MitchellClaimLossInfo.class);
			TableUtils.dropTable(connectionSource, MitchellClaimVehicleInfo.class, true);
			TableUtils.createTableIfNotExists(connectionSource, MitchellClaimVehicleInfo.class);

			String s1 = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><cla:MitchellClaim xmlns:cla=\"http://www.mitchell.com/examples/claim\"><cla:ClaimNumber>22c9c23bac142856018ce14a26b6c299</cla:ClaimNumber><cla:ClaimantFirstName>George</cla:ClaimantFirstName><cla:ClaimantLastName>Washington</cla:ClaimantLastName><cla:Status>OPEN</cla:Status><cla:LossDate>2014-07-09T17:19:13.631-07:00</cla:LossDate><cla:LossInfo><cla:CauseOfLoss>Collision</cla:CauseOfLoss><cla:ReportedDate>2014-07-10T17:19:13.676-07:00</cla:ReportedDate><cla:LossDescription>Crashed into an apple tree.</cla:LossDescription></cla:LossInfo><cla:AssignedAdjusterID>12345</cla:AssignedAdjusterID><cla:Vehicles><cla:VehicleDetails><cla:Vin>1M8GDM9AXKP042788</cla:Vin><cla:ModelYear>2015</cla:ModelYear><cla:MakeDescription>Ford</cla:MakeDescription><cla:ModelDescription>Mustang</cla:ModelDescription><cla:EngineDescription>EcoBoost</cla:EngineDescription><cla:ExteriorColor>Deep Impact Blue</cla:ExteriorColor><cla:LicPlate>NO1PRES</cla:LicPlate><cla:LicPlateState>VA</cla:LicPlateState><cla:LicPlateExpDate>2015-03-10-07:00</cla:LicPlateExpDate><cla:DamageDescription>Front end smashed in. Apple dents in roof.</cla:DamageDescription><cla:Mileage>1776</cla:Mileage></cla:VehicleDetails></cla:Vehicles></cla:MitchellClaim>";
			String s2 = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><cla:MitchellClaim xmlns:cla=\"http://www.mitchell.com/examples/claim\"><cla:ClaimNumber>22c9c23bac142856018ce14a26b6c299</cla:ClaimNumber><cla:AssignedAdjusterID>67890</cla:AssignedAdjusterID><cla:Vehicles><cla:VehicleDetails><cla:Vin>1M8GDM9AXKP042788</cla:Vin><cla:ExteriorColor>Competition Orange</cla:ExteriorColor><cla:LicPlateExpDate>2015-04-15-07:00</cla:LicPlateExpDate></cla:VehicleDetails></cla:Vehicles></cla:MitchellClaim>";

			JAXBContext context = JAXBContext.newInstance(ObjectFactory.class);
			Unmarshaller un = context.createUnmarshaller();
			MitchellClaimType old = ((JAXBElement<MitchellClaimType>) un
					.unmarshal(new ByteArrayInputStream(s1.getBytes(StandardCharsets.UTF_8)))).getValue();
			MitchellClaimType update = ((JAXBElement<MitchellClaimType>) un
					.unmarshal(new ByteArrayInputStream(s2.getBytes(StandardCharsets.UTF_8)))).getValue();
			/**
			 * use createMitchellClaim API
			 */
			mcService.createMitchellClaim(s1);
			mcService.updateClaim(s2);
			old.updateWithInstance(update);
			assertTrue(mcService.readByClaimNumber(old.getClaimNumber()).equals(old));

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			mcService.tearDown();
			try {
				if (connectionSource != null) {
					connectionSource.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}



	@Test
	public void testByGeneralCase() {
		/** prepare test XMLs **/
		System.out.println("auto generating test xml ... ");
		HashMap<String, MitchellClaimType> references = prepare(1000);
		HashMap<String, MitchellClaimType> updates = prepareUpdateXMLStrings(references.keySet());
		System.out.println("generating test xml finished ... ");

		/** create a service **/
		MitchellClaimService mcs = new MitchellClaimService(DATABASE_URL);
		mcs.setUp();

		try {

			JAXBContext jaxbContext = JAXBContext.newInstance(ObjectFactory.class);
			Marshaller jaxbMarshaller = jaxbContext.createMarshaller();

			// output pretty printed
			jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

			System.out.println("Here begins test... ");

			for (String key : references.keySet()) {

				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				jaxbMarshaller.marshal(updates.get(key), baos);

				/** use updateClaim API to update data base **/
				mcs.updateClaim(baos.toString("UTF-8"));
				/** read the updated value **/
				MitchellClaimType updatedMc = mcs.readByClaimNumber(key);
				/** update the reference **/
				MitchellClaimType oldMc = references.get(key);
				oldMc.updateWithInstance(updates.get(key));
				/** check result **/
				assertTrue(updatedMc.equals(oldMc));

			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		mcs.tearDown();
	}

	@AfterClass
	public static void cleanDatabase() {

	}

	private static final String DATABASE_URL = "jdbc:sqlite:mem:test";
}
