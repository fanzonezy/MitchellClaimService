# MitchellClaimService

Part One: Basic System Architecture Spec

This is a eclipse project, simply download it and import it to eclipse and run testClient(in TestCases package). 

Here are THREE packages: 

MitchellClaimORMModels: includes all the ORM classes(models).

MitchellClaimServiceUtils: includes all the data types sufficient to represent a claim

MitchellClaimService: includes one Java file implementing all the API. 

TestCases: inlcudes all the unit test and a test client. 

(1) Interanl Representation:

I used a user defined class MitchellClaimType to represent a claim. The definition of MitchellClaimType is based on other sub-type defined by xsd file: such as LossInfoType, VehicleInfoType, etc. What I did here is actually convert a XML schema to a bunch of JavaBean classes. All those classes are is in MitchellClaimServiceUtils.

Here are the mapping relations between types defined in xsd file and Java(Bean) classes:

      xsd                       java object

MitchellClaimType  -->  MitchellClaimType(JavaBean class)

StatusCode         -->  StatusCode(Java enum type)

CauseOfLossCode    -->  CauseOfLossCode(Java enum type)

LossInfoType       -->  LossInfoType(JavaBean class)

VehicleListType    -->  VehicleListType(JavaBean class)

VehicleInfoType    -->  VehicleInfoType(JavaBean class)

Other primitive types is converted by JAXB standard, like string is converted to Java String, dateTime and date are converted to XMLGregorianCalendar. 

(2) ORM model and Table Design

In this project, ORM model is used. ORM model classes are defined in MitchellClaimORMModels. Obviously, we can not squeeze all claim information in a single table. Here, we simply used three tables: BasicInfo, LossInfo, VehicleInfo. What is worth mention here is that the ORM model classes/instances are actually the programmable representations of tables.

Part Two: Implementation and Components:

ORM: ORMLite

DataBase driver: sqlite

XML marshaler and unmarshaler: jaxb

Unit Test; JUnit

The work flow of implementation is: 

(1) Define the behavior of APIs (May also made test cases, if working in TDD mode).

(2) Design data base schema(ORM classes).

(3) Define internal representation of claim XML(which is done automatically through JAXB).

(4) implment API.

Part Three: API Specification:

(1) createMtchellClaim:

input: String

output: None
throws: IllegalArgumentException, SQLException

spec: if seccessful, a claim will be persisted to back store. if the input is invalid, an IllegalArgumentException will be raised. if one of the write option to date base is failed, a SQLException will be raised. This API implement by transaction. 

(2) readByClaimNumber:

input: string 
output: MitchellClaimType

spec: if seccessful, this API will return a MitchellClaimType instance which is uniquely determined by claim number.If the input claim number is invalid or the claim number is not in data base, an null will be returned. 

(3) readByLossDate

input: (1) start date: Date (2) end date: Date
output: List<MitchellClaimType>

spec: if seccessful, a list of MitchellClaimType instance will be returned. if there is no claim with a loss date within the range an empty list will be return. If the input arguments are invalid, an empty list will be returned.

(4) updateClaim

input: string
output: None
throws: IllegalArgumentException, SQLException

spec: if seccessful, a claim in back store will be updated by input XML string. If the input is invalid, an IllegalArgumentException will be raised. if one of the write option to date base is failed, a SQLException will be raised. This API implement by transaction. If the claim you want to update is not in date base, an SQLException will be raised.    

(5) readVehicleByClaimNumberAndVin

input: (1) claim number: string (2) vin: string
output: VehicleInfoType

spec: if seccessful, an instance of VehicleInfo type will be returned. If not, a null object will be returned.

(6) deleteByClaimNumber

input: string
output: None
throws: IllegalArgumentException, SQLException

spec: if successful, a claim will be deleted from back store. If the input is invalid, an IllegalArgumentException will be raised. if one of the write option to date base is failed, a SQLException will be raised. This API implement by transaction.

Part Four: Test Client Spec

I used JUnit test frame work to test those APIs. By simply run TestClient you can run all the tests. Besides the sample XML file provided. I designed some algorithms to randomly generate legal XML file to test my API. those algorithms are packed in TestXMLStringGenerator class which is in test case package.

Part Five: What to Improve

(1) Work flow: 

If we were ask to do this task again I would flow a TTD(Test Driven Design) work flow. I would first determine how should my API behave and what test should them pass. Cause I did't determine API behavior very well at first, I change the API several times, what is annoying is that once my API has changed, I should also change the test cases in order to test the new version API. This wasted me a lot of time.

(2) Take good care of Exception:

Because the limit of time, I haven't handled exception in a careful way. When I shoudl catch an Exception, I didn't distinguish the category of exception, but just did something like: 
      catch(Exception e)}{
            e.printStackTrace();      
      }
If you had enough time I will hanled this is a more careful way, at least I will catch different kinds of exception and hanle them differently.

Also, I may design some self-defined exception to give upper level API from information when a exception occurs. 

(3) Try to avoid over design.

At first, I tried to design those APIs such that those APIs are both independent with XML format and with database structure(the definition of tables.). The first one is relatively easy to achieve, but the latter one is nearly impossible for me at this point. At the first week I spent a lot time to figure out a structure which supports this attributes. But, after several days, I realized it may be a little over-design. Since it's reasonable for API to know the structure of the datebase it operates on. Sometime, it may be very hard to perfectly decouple all the components in a system and some dependency may always exist. 

(4) Write more test cases.

Because the limit of time. I just write some basic test cases which may not test those APIs exaustively. 




