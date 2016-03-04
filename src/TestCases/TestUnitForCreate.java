package TestCases;

import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Unmarshaller;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.logger.LocalLog;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import MitchellClaimServiceUtils.*;
import MitchellClaimService.*;
import MitchellClaimORMModels.*;

public class TestUnitForCreate {

	final String DATABASE_URL = "jdbc:sqlite:mem:test";

	@BeforeClass
	public static void initializeDataBase() {
		System.setProperty(LocalLog.LOCAL_LOG_LEVEL_PROPERTY, "ERROR");
	}

	@Before
	public void prepare() {
		ConnectionSource connectionSource = null;

		try {
			/**
			 * initialize ORMlite DAO for verifying DB status
			 */
			connectionSource = new JdbcConnectionSource(DATABASE_URL);

			/** create tables for test **/
			TableUtils.createTableIfNotExists(connectionSource, MitchellClaimBasicInfo.class);
			TableUtils.dropTable(connectionSource, MitchellClaimLossInfo.class, true);
			TableUtils.createTableIfNotExists(connectionSource, MitchellClaimLossInfo.class);
			TableUtils.dropTable(connectionSource, MitchellClaimVehicleInfo.class, true);
			TableUtils.createTableIfNotExists(connectionSource, MitchellClaimVehicleInfo.class);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
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
	public void testWithInvalidXMLString() {
		ConnectionSource connectionSource = null;

		/**
		 * create 1 random MitchellClaim XML String to test createMitchellClaim
		 * API
		 */
		String XMLString = null;
		MitchellClaimService mcService = new MitchellClaimService(DATABASE_URL);
		mcService.setUp();

		try {

			/**
			 * initialize ORMlite DAO for verifying DB status
			 */
			connectionSource = new JdbcConnectionSource(DATABASE_URL);

			/**
			 * use createMitchellClaim API
			 */
			mcService.createMitchellClaim(XMLString);

			/**
			 * verify DB status
			 */
			List<Long> cnts = mcService.countOf();

			// For MitchellBasicInfo table must return exact 1 tuple
			assertTrue(cnts.get(0) == 0);
			// For MitchellLossInfo table must return exact 1 tuple
			assertTrue(cnts.get(1) == 0);
			// For MitchellVehicleInfo table must return the same number as the
			// number of vehicles
			assertTrue(cnts.get(2) == 0);

		

			mcService.tearDown();
		} catch (IllegalArgumentException e) {
			//e.printStackTrace();
			System.out.println("catched.");
		} catch (Exception e){
			e.printStackTrace();
		}finally {
		
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
	public void testWithSingleClaim() {
		ConnectionSource connectionSource = null;

		/**
		 * create 1 random MitchellClaim XML String to test createMitchellClaim
		 * API
		 */
		String[] testXMLStrings = TestXMLGenerator.generateTestXMLString(1);
		MitchellClaimService mcService = new MitchellClaimService(DATABASE_URL);
		mcService.setUp();

		try {

			/**
			 * initialize ORMlite DAO for verifying DB status
			 */
			connectionSource = new JdbcConnectionSource(DATABASE_URL);
			Dao<MitchellClaimBasicInfo, String> bifDao = DaoManager.createDao(connectionSource,
					MitchellClaimBasicInfo.class);
			Dao<MitchellClaimLossInfo, String> lifDao = DaoManager.createDao(connectionSource,
					MitchellClaimLossInfo.class);
			Dao<MitchellClaimVehicleInfo, Void> vifDao = DaoManager.createDao(connectionSource,
					MitchellClaimVehicleInfo.class);

			JAXBContext context = JAXBContext.newInstance(ObjectFactory.class);
			Unmarshaller un = context.createUnmarshaller();

			for (String XMLString : testXMLStrings) {

				/**
				 * create reference MitchellClaimType instance
				 */

				MitchellClaimType mct = ((JAXBElement<MitchellClaimType>) un
						.unmarshal(new ByteArrayInputStream(XMLString.getBytes(StandardCharsets.UTF_8)))).getValue();
				/**
				 * use createMitchellClaim API
				 */
				mcService.createMitchellClaim(XMLString);

				/**
				 * verify DB status
				 */
				List<MitchellClaimBasicInfo> l_basicInfo_t = bifDao
						.queryForEq(MitchellClaimBasicInfo.CLAIM_NUMBER_FIELD_NAME, mct.getClaimNumber());
				List<MitchellClaimLossInfo> l_lossInfo_t = lifDao
						.queryForEq(MitchellClaimLossInfo.CLAIM_NUMBER_FIELD_NAME, mct.getClaimNumber());
				List<MitchellClaimVehicleInfo> l_vehicleInfo_t = vifDao
						.queryForEq(MitchellClaimVehicleInfo.CLAIM_NUMBER_FIELD_NAME, mct.getClaimNumber());

				// For MitchellBasicInfo table must return exact 1 tuple
				assertEquals(1, l_basicInfo_t.size());
				// For MitchellLossInfo table must return exact 1 tuple
				assertEquals(1, l_lossInfo_t.size());
				// For MitchellVehicleInfo table must return the same number as
				// the number of vehicles
				assertEquals(mct.getVehicles().getVehicleDetails().size(), l_vehicleInfo_t.size());

				// all fields must be the same as the reference
				MitchellClaimType retrievedMct = MitchellClaimService.toMitchellClaimType(l_basicInfo_t.get(0),
						l_lossInfo_t.get(0), l_vehicleInfo_t);

			
				assertTrue(mct.equals(retrievedMct));

			}
			mcService.tearDown();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
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
	public void testWithMultipleClaim() {
		ConnectionSource connectionSource = null;

		/**
		 * create 100 random MitchellClaim XML String to test
		 * createMitchellClaim API
		 */
		String[] testXMLStrings = TestXMLGenerator.generateTestXMLString(100);
		MitchellClaimService mcService = new MitchellClaimService(DATABASE_URL);
		mcService.setUp();

		try {

			/**
			 * initialize ORMlite DAO for verifying DB status
			 */
			connectionSource = new JdbcConnectionSource(DATABASE_URL);
			Dao<MitchellClaimBasicInfo, String> bifDao = DaoManager.createDao(connectionSource,
					MitchellClaimBasicInfo.class);
			Dao<MitchellClaimLossInfo, String> lifDao = DaoManager.createDao(connectionSource,
					MitchellClaimLossInfo.class);
			Dao<MitchellClaimVehicleInfo, Void> vifDao = DaoManager.createDao(connectionSource,
					MitchellClaimVehicleInfo.class);

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
				 * verify DB status
				 */
				List<MitchellClaimBasicInfo> l_basicInfo_t = bifDao
						.queryForEq(MitchellClaimBasicInfo.CLAIM_NUMBER_FIELD_NAME, mct.getClaimNumber());
				List<MitchellClaimLossInfo> l_lossInfo_t = lifDao
						.queryForEq(MitchellClaimLossInfo.CLAIM_NUMBER_FIELD_NAME, mct.getClaimNumber());
				List<MitchellClaimVehicleInfo> l_vehicleInfo_t = vifDao
						.queryForEq(MitchellClaimVehicleInfo.CLAIM_NUMBER_FIELD_NAME, mct.getClaimNumber());

				// For MitchellBasicInfo table must return exact 1 tuple
				assertEquals(1, l_basicInfo_t.size());
				// For MitchellLossInfo table must return exact 1 tuple
				assertEquals(1, l_lossInfo_t.size());
				// For MitchellVehicleInfo table must return the same number as
				// the number of vehicles
				assertEquals(mct.getVehicles().getVehicleDetails().size(), l_vehicleInfo_t.size());

				MitchellClaimType retrievedMct = MitchellClaimService.toMitchellClaimType(l_basicInfo_t.get(0),
						l_lossInfo_t.get(0), l_vehicleInfo_t);

				// retrievedMct.display();
				assertTrue(mct.equals(retrievedMct));

			}
			mcService.tearDown();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (connectionSource != null) {
					connectionSource.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	@AfterClass
	public static void cleanDatabase() {
	}
}
