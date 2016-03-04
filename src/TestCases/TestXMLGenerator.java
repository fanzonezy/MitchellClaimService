package TestCases;

import java.awt.image.RescaleOp;
import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.TimeZone;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import java.util.Date;

import MitchellClaimServiceUtils.*;

public class TestXMLGenerator {

	public static XMLGregorianCalendar randomDateTime() {
		GregorianCalendar gc = new GregorianCalendar();
		Random rand = new Random();

		int year = rand.nextInt(16) + 2000;
		gc.set(Calendar.YEAR, year);
		int dayOfYear = rand.nextInt(gc.getActualMaximum(Calendar.DAY_OF_YEAR)) + 1;
		gc.set(Calendar.DAY_OF_YEAR, dayOfYear);

		gc.set(Calendar.HOUR, rand.nextInt(24));
		gc.set(Calendar.MINUTE, rand.nextInt(60));
		gc.set(Calendar.SECOND, rand.nextInt(60));
		gc.set(Calendar.MILLISECOND, rand.nextInt(1000));

		try {
			return DatatypeFactory.newInstance().newXMLGregorianCalendar(gc);
		} catch (DatatypeConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		;
		return null;
	}

	public static String[] randomSequenceGenerator(Integer nBit, int N, boolean allLetters) {
		String[] rtn = new String[N];
		HashSet<String> hSet = new HashSet<String>();

		Random rand = new Random();
		int i = 0;
		while (i < N) {
			String cn = "";
			for (int j = 0; j < nBit; j++) {
				if (!allLetters) {
					int turn = rand.nextInt(2); // determine to add a integer of
												// a letter
					if (turn == 0) { // add a integer
						cn += Integer.toString(rand.nextInt(10));
					} else { // add a letter
						cn += Character.toString((char) (97 + rand.nextInt(26)));
					}
				} else {
					cn += Character.toString((char) (97 + rand.nextInt(26)));
				}
			}

			if (hSet.contains(cn)) {
				continue;
			}

			rtn[i] = cn;
			i++;
		}

		return rtn;
	}

	/**
	 * randomly determine if a method will appear
	 * 
	 * @return
	 */
	public static boolean appear() {
		Random rand = new Random();
		return rand.nextInt(2) == 1;
	}

	public static String[] generateTestXMLString(int N) {
		String[] rtn = new String[N];
		String[] causeOfLoss = { "Collision", "Explosion", "Fire", "Hail", "Mechanical Breakdown", "Other" };

		/**
		 * generate claim number and Vin (no duplicate)
		 */
		String[] claimNumbers = randomSequenceGenerator(32, N, false);
		String[] vins = randomSequenceGenerator(17, 3 * N, false);
		int currVinIndex = 0;

		Random rand = new Random();
		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(ObjectFactory.class);
			Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
			jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

			for (int i = 0; i < N; ++i) {
				MitchellClaimType mc = new MitchellClaimType();
				mc.setClaimNumber(claimNumbers[i]);

				// generate name with length varies from 5 - 15
				mc.setClaimantFirstName(randomSequenceGenerator(rand.nextInt(11) + 5, 1, true)[0]);
				mc.setClaimantLastName(randomSequenceGenerator(rand.nextInt(11) + 5, 1, true)[0]);
				mc.setLossDate(randomDateTime());

				mc.setAssignedAdjusterID((long) (rand.nextInt(1000000) + 10000));
				mc.setStatus(rand.nextInt(2) == 0 ? StatusCode.OPEN : StatusCode.CLOSED);

				LossInfoType lossInfo = new LossInfoType();
				lossInfo.setCauseOfLoss(CauseOfLossCode.fromValue(causeOfLoss[rand.nextInt(6)]));
				lossInfo.setLossDescription(randomSequenceGenerator(256, 1, true)[0]);
				lossInfo.setReportedDate(randomDateTime());
				mc.setLossInfo(lossInfo);

				int numOfVehicles = rand.nextInt(3) + 1;
				List<VehicleInfoType> l_vehicleInfo = new ArrayList<VehicleInfoType>();
				VehicleListType vlt = new VehicleListType();

				for (int k = 0; k < numOfVehicles; ++k) {
					VehicleInfoType vehicleInfo = new VehicleInfoType();
					vehicleInfo.setDamageDescription(randomSequenceGenerator(256, 1, true)[0]);
					vehicleInfo.setExteriorColor(randomSequenceGenerator(256, 1, true)[0]);
					vehicleInfo.setEngineDescription(randomSequenceGenerator(10, 1, true)[0]);
					vehicleInfo.setLicPlate(randomSequenceGenerator(7, 1, false)[0]);
					vehicleInfo.setLicPlateExpDate(randomDateTime());
					vehicleInfo.setLicPlateState(randomSequenceGenerator(10, 1, true)[0]);
					vehicleInfo.setMakeDescription(randomSequenceGenerator(256, 1, true)[0]);
					vehicleInfo.setMileage(rand.nextInt(100000));
					vehicleInfo.setModelDescription(randomSequenceGenerator(256, 1, true)[0]);
					vehicleInfo.setModelYear(rand.nextInt(66) + 1950);
					vehicleInfo.setVin(vins[currVinIndex]);
					currVinIndex += 1;
					l_vehicleInfo.add(vehicleInfo);
				}
				vlt.setVehicleDetails(l_vehicleInfo);
				mc.setVehicles(vlt);

				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				jaxbMarshaller.marshal(mc, baos);
				rtn[i] = baos.toString("UTF-8");

			}

		} catch (JAXBException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		return rtn;

	}

	public static String[] generateTestPartialXMLString(int N) {
		String[] rtn = new String[N];
		String[] causeOfLoss = { "Collision", "Explosion", "Fire", "Hail", "Mechanical Breakdown", "Other" };

		/**
		 * generate claim numbers and vins (no duplicate)
		 */
		String[] claimNumbers = randomSequenceGenerator(32, N, false);
		String[] vins = randomSequenceGenerator(17, 3 * N, false);
		int currVinIndex = 0;

		Random rand = new Random();

		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(ObjectFactory.class);
			Marshaller jaxbMarshaller = jaxbContext.createMarshaller();

			for (int i = 0; i < N; ++i) {
				MitchellClaimType mc = new MitchellClaimType();
				mc.setClaimNumber(claimNumbers[i]); /** defined as required **/
				// generate a letter with length varies from 5 - 15
				mc.setClaimantFirstName(appear() ? randomSequenceGenerator(rand.nextInt(11) + 5, 1, true)[0] : null);
				mc.setClaimantLastName(appear() ? randomSequenceGenerator(rand.nextInt(11) + 5, 1, true)[0] : null);
				mc.setLossDate(appear() ? randomDateTime() : null);

				mc.setAssignedAdjusterID(appear() ? (long) (rand.nextInt(1000000) + 10000) : null);
				mc.setStatus(appear() ? (rand.nextInt(2) == 0 ? StatusCode.OPEN : StatusCode.CLOSED) : null);

				if (appear()) {
					LossInfoType lossInfo = new LossInfoType();
					lossInfo.setCauseOfLoss(appear() ? CauseOfLossCode.fromValue(causeOfLoss[rand.nextInt(6)]) : null);
					lossInfo.setLossDescription(appear() ? randomSequenceGenerator(256, 1, true)[0] : null);
					lossInfo.setReportedDate(appear() ? randomDateTime() : null);
					mc.setLossInfo(lossInfo);
				} else {
					mc.setLossInfo(null);
				}

				if (appear()) {
					int numOfVehicles = rand.nextInt(3) + 1;
					List<VehicleInfoType> l_vehicleInfo = new ArrayList<VehicleInfoType>();
					VehicleListType vlt = new VehicleListType();

					for (int k = 0; k < numOfVehicles; ++k) {
						VehicleInfoType vehicleInfo = new VehicleInfoType();
						vehicleInfo.setDamageDescription(appear() ? randomSequenceGenerator(256, 1, true)[0] : null);
						vehicleInfo.setExteriorColor(appear() ? randomSequenceGenerator(256, 1, true)[0] : null);
						vehicleInfo.setEngineDescription(appear() ? randomSequenceGenerator(10, 1, true)[0] : null);
						vehicleInfo.setLicPlate(appear() ? randomSequenceGenerator(7, 1, false)[0] : null);
						vehicleInfo.setLicPlateExpDate(appear() ? randomDateTime() : null);
						vehicleInfo.setLicPlateState(appear() ? randomSequenceGenerator(10, 1, true)[0] : null);
						vehicleInfo.setMakeDescription(appear() ? randomSequenceGenerator(256, 1, true)[0] : null);
						vehicleInfo.setMileage(appear() ? rand.nextInt(100000) : null);
						vehicleInfo.setModelDescription(appear() ? randomSequenceGenerator(256, 1, true)[0] : null);
						vehicleInfo.setModelYear(rand.nextInt(66)
								+ 1950); /** defined as required **/
						vehicleInfo.setVin(
								vins[currVinIndex]); /**
														 * it's reasonable to
														 * always have VIN
														 **/
						currVinIndex += 1;
						l_vehicleInfo.add(vehicleInfo);
					}
					vlt.setVehicleDetails(l_vehicleInfo);
					mc.setVehicles(vlt);
				} else {
					mc.setVehicles(null);
				}

				// output pretty printed
				jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				jaxbMarshaller.marshal(mc, baos);
				rtn[i] = baos.toString("UTF-8");

			}

		} catch (JAXBException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		return rtn;
	}

	public static void main(String[] args) {
		String[] strs = generateTestPartialXMLString(3);
		for (String str : strs) {
			System.out.println(str);
		}

	}

}
