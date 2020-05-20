/*
 * (C) Copyright IBM Corp. 2020.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.io.InputStream;




import com.ibm.cloud.sdk.core.http.Response;
import com.ibm.cloud.sdk.core.http.ServiceCall;
import com.ibm.cloud.sdk.core.security.Authenticator;
import com.ibm.cloud.sdk.core.security.BasicAuthenticator;
import com.ibm.watson.health.acd.v1.AnnotatorForClinicalData;
import com.ibm.watson.health.acd.v1.model.AcdFlow;
import com.ibm.watson.health.acd.v1.model.AcdProfile;
import com.ibm.watson.health.acd.v1.model.AcdCartridges;
import com.ibm.watson.health.acd.v1.model.AnalyzeWithFlowOptions;
import com.ibm.watson.health.acd.v1.model.Annotator;
import com.ibm.watson.health.acd.v1.model.AnnotatorFlow;
import com.ibm.watson.health.acd.v1.model.ConfigurationEntity;
import com.ibm.watson.health.acd.v1.model.Annotator.Name;
import com.ibm.watson.health.acd.v1.model.CartridgesGetOptions;
import com.ibm.watson.health.acd.v1.model.CartridgesGetIdOptions;
import com.ibm.watson.health.acd.v1.model.CartridgesPostMultipartOptions;
import com.ibm.watson.health.acd.v1.model.CartridgesPutMultipartOptions;
import com.ibm.watson.health.acd.v1.model.DeployCartridgeResponse;
import com.ibm.watson.health.acd.v1.model.DeployCartridgeOptions;
import com.ibm.watson.health.acd.v1.model.ContainerGroup;
import com.ibm.watson.health.acd.v1.model.CreateFlowsOptions;
import com.ibm.watson.health.acd.v1.model.CreateProfileOptions;
import com.ibm.watson.health.acd.v1.model.DeleteFlowsOptions;
import com.ibm.watson.health.acd.v1.model.DeleteProfileOptions;
import com.ibm.watson.health.acd.v1.model.Flow;
import com.ibm.watson.health.acd.v1.model.FlowEntry;
import com.ibm.watson.health.acd.v1.model.GetFlowsByIdOptions;
import com.ibm.watson.health.acd.v1.model.GetFlowsOptions;
import com.ibm.watson.health.acd.v1.model.GetProfileOptions;
import com.ibm.watson.health.acd.v1.model.GetProfilesOptions;
import com.ibm.watson.health.acd.v1.model.SymptomDisease;
import com.ibm.watson.health.acd.v1.model.UpdateProfileOptions;
import com.ibm.watson.health.acd.v1.model.Concept;
import com.ibm.watson.health.acd.v1.util.FlowUtil;

/**
 * Sample ACD Java SDK Calls
 */
public class AnnotatorForClinicalDataExample {
	

	public static String ACD_APIKEY="xxx";
	public static String ACD_URL="https://us-south.wh-acd.cloud.ibm.com/wh-acd/api";
	public static String version = "2020-05-18";
	public static String name="AnnotatorForClinicalData";
	
	public static String Constants_PROFILE_ID  = "p51c";
	public static String Constants_FLOW_ID  = "flow51asdf";

	public static String FLOW_ID_DEFAULT  = "wh_acd.ibm_clinical_insights_v1.0_standard_flow";
	public static String FLOW_ID_COVID  = "wh_acd.ibm_covid-19_research_v1.0_covid-19_flow_flow";

	// [] 
	public static AnnotatorForClinicalData acd = null;
	public static Annotator annotatorSC = null;
	public static Annotator annotatorCD = null;
	
	static {

		Authenticator authenticator = new BasicAuthenticator("apikey", ACD_APIKEY);
		acd = new AnnotatorForClinicalData(
				version,
				AnnotatorForClinicalData.DEFAULT_SERVICE_NAME,
				authenticator);
		acd.setServiceUrl(ACD_URL);

		System.out.println(acd.getName());
		System.out.println(acd.getVersion());

		/**
		 * Annotator Spell Cchecker
		 */
		annotatorSC = new Annotator.Builder()
				.name(Annotator.Name.SPELL_CHECKER)
				.build();
		annotatorCD = new Annotator.Builder()
				.name(Annotator.Name.CONCEPT_DETECTION)
				.build();

	}

	public static void main(String[] args) throws Exception {

		/**
		 * analyze
		 */
		//analyze();		
		//analyzeWithFlowId(FLOW_ID_DEFAULT);
		
		/**
		 * profiles
		 */
		getProfiles(); // read all profiles
		String sProfileId ="p61a"+System.currentTimeMillis();		
		createProfile(sProfileId);
		getProfile(sProfileId);
		deleteProfile(sProfileId);
		//String sProfileName=sProfileId+"-name";
		//String sProfileDescription=sProfileId+"-description";
		//updateProfile( sProfileId, sProfileName, sProfileDescription );
		//getProfile(sProfileId);
		//deleteProfile(sProfileId);


		
		/**
		 * FLOWS
		 */
		getFlows();
		String sFlowId ="f61a"+System.currentTimeMillis();		
		createFlow(sFlowId);
		getFlow(sFlowId);
		deleteFlow(sFlowId);
		
		 
		/**
		 * CARTRIDGES
		 */
		// cartridges
		// getCartridges() -> not implemented yet
		// 
		//String sZipFile="testacd_deploycart_dict_v1.0.zip";
		String sZipFile="testacd_deploycart_attr_v1.0.zip";		
		//deployCartridge(sZipFile);
		redeployCartridge(sZipFile);
		legacyDeploy(sZipFile);
		getCartridge(sZipFile.replace(".zip", ""));


	}

	/*
	 * Annotator for Clinical Data Text Analysis Calls
	 */

	public static void analyze() {

		List<String> annotators = new ArrayList<String>(
				Arrays.asList(
						Annotator.Name.SYMPTOM_DISEASE,
						Annotator.Name.MEDICATION,
						Annotator.Name.HYPOTHETICAL));

		Flow flow = new FlowUtil.Builder().annotators(annotators).build();

		String text = "Patient has cancer.";

		ContainerGroup response = acd.analyze(text, flow);
		System.out.println(response);

		List<SymptomDisease> symptomDisease = response.getSymptomDisease();
		System.out.println(symptomDisease);
		
	}

	// POST /analyze/{flowId}
	public static void analyzeWithFlowId(String sFlowId) {
				
		// [] checking flowId
		sFlowId = (sFlowId == null) ? FLOW_ID_DEFAULT : sFlowId;
		sFlowId = (sFlowId.length() == 0) ? FLOW_ID_DEFAULT : sFlowId;
		
		//acd.analyzeWithFlow(flowId, text)

		//.contentType(Constants.CONTENT_TYPE)		
		AnalyzeWithFlowOptions options = new AnalyzeWithFlowOptions.Builder(sFlowId)
				.text("text/plain")
				.build();

		ServiceCall<ContainerGroup> sc = acd.analyzeWithFlow(options);
		Response<ContainerGroup> response = sc.execute();
		ContainerGroup containerGroup = response.getResult();
		List<Concept> concepts = containerGroup.getConcepts();
		for( Concept concept : concepts ) 
			System.out.println( concept );

	}

	/*
	 * Profile CRUD Operations
	 */

	// GET /profiles
	public static void getProfiles() {

		GetProfilesOptions options = new GetProfilesOptions.Builder().build();
		ServiceCall<Map<String, AcdProfile>> serviceCall = acd.getProfiles(options);
		Response<Map<String, AcdProfile>> response = serviceCall.execute();
		Map<String, AcdProfile> mapStrAcdProfile = response.getResult(); 
		for( String strKey :  mapStrAcdProfile.keySet() ) {
			System.out.println( strKey + " :  " + mapStrAcdProfile.get(strKey) );	
		}
		
	}
	
	
	// GET /profiles/{id}
	public static void getProfile(String sId) {
				
		GetProfileOptions options = new GetProfileOptions.Builder().id(sId).build();
		ServiceCall<AcdProfile> sc = acd.getProfile(options);
		Response<AcdProfile> response = sc.execute();
		AcdProfile profile = response.getResult();

		if( profile == null )
			return;

		System.out.println(profile);
		System.out.println(profile.id() + " " +  Constants_PROFILE_ID);
		System.out.println(profile.name());
		if (profile.description() != null) {
			System.out.println( profile.description().length() > 0); 
		}

		List<Annotator> annotators = profile.annotators();
		System.out.println( annotators); 
		for (Annotator annotator : annotators) {
			System.out.println(annotator.name());
			System.out.println(annotator.description());
		}		

	}
	

	// POST /profiles
	public static void createProfile(String sProfileId) {
		
		//.addAnnotators(		
		CreateProfileOptions options = new CreateProfileOptions
				.Builder()
				.id(sProfileId)
				.annotators(new ArrayList<Annotator>(Arrays.asList(annotatorSC)))
				.build();
		ServiceCall sc = acd.createProfile(options);
		Response<AcdProfile> response = sc.execute();
		AcdProfile profile = response.getResult();
		if( profile == null )
			return;
		System.out.println(profile.id() );


	}

	// PUT /profiles
	/**
	 * 
	 * @param sProfileId            : required
	 * @param sProfileName          : optional
	 * @param sProfileDescription   : optional
	 */
	public static void updateProfile( String sProfileId, String sProfileName, String sProfileDescription ) {
				
		sProfileName        = (sProfileName == null ) ? "" : sProfileName;
		sProfileDescription = (sProfileDescription == null ) ? "" : sProfileDescription; 
		
		UpdateProfileOptions options = new UpdateProfileOptions
				.Builder()
				.id(sProfileId)
				.newId(sProfileId+"-new")
				.newName(sProfileName)
				.newDescription(sProfileDescription)
				.addNewAnnotators(annotatorSC)
				.build();
		//GetProfileOptions options = new GetProfileOptions.Builder().id(Constants_PROFILE_ID).build();
		ServiceCall sc = acd.updateProfile(options);
		Response<AcdProfile> response = sc.execute();
		AcdProfile profile = response.getResult();	
		if(profile == null)
			return;		
		
	}
	

	// DELETE /profiles/{id}
	public static void deleteProfile(String sProfileId) {
		
		DeleteProfileOptions options = new DeleteProfileOptions
				.Builder()
				.id(sProfileId)
				.build();

		ServiceCall sc = acd.deleteProfile(options);
		Response<AcdProfile> response = sc.execute();
		AcdProfile profile = response.getResult();

		if( profile == null )
			return;		

	}

	/*
	 * Flow CRUD Operations
	 */

	// GET /flows
	public static void getFlows() {
		
		GetFlowsOptions options = new GetFlowsOptions.Builder().build();
		ServiceCall<Map<String, AcdFlow>> serviceCall = acd.getFlows(options);
		Response<Map<String, AcdFlow>> response = serviceCall.execute();
		Map<String, AcdFlow> mapStrAcdFlow = response.getResult(); 
		for( String strKey :  mapStrAcdFlow.keySet() ) {
			System.out.println(  strKey + " : " + mapStrAcdFlow.get(strKey) );	
		}

	}

	// GET /flows/{id}
	public static void getFlow(String sId) {
		
		GetFlowsByIdOptions options = new GetFlowsByIdOptions.Builder()
				.id(sId)
				.build();
		ServiceCall<AcdFlow> sc = acd.getFlowsById(options);
		Response<AcdFlow> response = sc.execute();
		AcdFlow flow = response.getResult();		
		if( flow == null )
			return;

		/*
		System.out.println(flow);
		System.out.println(flow.id() + " " +  FLOW_ID_DEFAULT);
		System.out.println(flow.name());
		if ( flow.description() != null) {
			System.out.println( flow.description().length() > 0); 
		}

		List<AnnotatorFlow> annotatorFlowList = flow.annotatorFlows();
		for ( AnnotatorFlow annotatorFlow : annotatorFlowList ) {
			System.out.println(annotatorFlow.profile());
			System.out.println(annotatorFlow.flow());
			Flow annotatorFlowFlow = annotatorFlow.flow();
			List<FlowEntry> flowEntryList = annotatorFlowFlow.elements();
			for( FlowEntry flowEntry : flowEntryList ) {
				Annotator annotator =  flowEntry.annotator();
				System.out.println( " annotator Name : " + annotator.name() );
			}
		}
		*/

	}

	// POST /flows
	public static void createFlow(String sId) {
		
		//.addAnnotators(
		//.annotators(new ArrayList<Annotator>(Arrays.asList(annotatorSC)))	    
		CreateFlowsOptions options = new CreateFlowsOptions
				.Builder()
				.acdFlow( createFlowSample(sId) )
				.build();

		ServiceCall sc = acd.createFlows(options);
		Response<AcdProfile> response = sc.execute();
		AcdProfile profile = response.getResult();

		if( profile == null )
			return;		

	}
	
	/**
	 * ACD Service Flow
	 */	
	private static AcdFlow createFlowSample(String sId) {

		
		/**ConfigurationEntity configurationEntityModel = new ConfigurationEntity.Builder()
				.id("testString")
				.type("testString")
				.uid(Long.valueOf("26"))
				.mergeid(Long.valueOf("26"))
				.build();
				**/

		FlowEntry flowEntryModel = new FlowEntry.Builder().annotator(annotatorSC).build();	    

		Flow flowModel = new Flow.Builder()
				.elements(new ArrayList<FlowEntry>(Arrays.asList(flowEntryModel)))
				.async(true)
				.build();


		AnnotatorFlow annotatorFlowModel = new AnnotatorFlow.Builder()
				.profile("testString")
				.flow(flowModel)
				.build();

		AcdFlow acdFlowModel = new AcdFlow.Builder()
				.id(sId)
				//.description("testString")
				//.publishedDate("testString")
				//.publish(true)
				//.version("testString")
				//.cartridgeId("testString")
				.annotatorFlows(new ArrayList<AnnotatorFlow>(Arrays.asList(annotatorFlowModel)))
				.build();

		return acdFlowModel;
	}
	

	// PUT /flows
	public static void updateFlow() {

	}

	// DELETE /flows/{id}
	public static void deleteFlow(String sId) {
		
		DeleteFlowsOptions options = new DeleteFlowsOptions
				.Builder()
				.id(sId)
				.build();

		ServiceCall sc = acd.deleteFlows(options);
		Response<AcdFlow> response = sc.execute();
		AcdFlow flow = response.getResult();
		if( flow == null )
			return;

	}

	// DELETE /user_data
	public static void deleteTenantConfigs() {

	}

	// GET /status/health_check
	public static void getHealthCheck() {

	}
	
	
	/**
	 * Cartridges
	 */
	
	// GET /cartridges
	public static void getCartridges() {
		
		//CartridgesGetOptions options = new CartridgesGetOptions.Builder().build();
		// https://github.com/IBM/whcs-java-sdk/blob/master/modules/annotator-for-clinical-data/src/main/java/com/ibm/watson/health/acd/v1/model/CartridgesGetOptions.java
		
	}

	// GET /cartridges/{id}
	public static void getCartridge(String sId) {
		
		CartridgesGetIdOptions options = new CartridgesGetIdOptions.Builder()
				.id(sId)
				.build();
		ServiceCall<AcdCartridges> sc = acd.cartridgesGetId(options);
		Response<AcdCartridges> response = sc.execute();
		AcdCartridges cartridges = response.getResult();		
		if( cartridges == null )
			return;
		
		System.out.println ( " id  : " + cartridges.getId() );
		System.out.println ( " correlationId: " +  cartridges.getCorrelationId() ); 

	}
	
	

	// POST /cartridges
	public static void deployCartridge(String sZipFile) throws Exception {
				
		InputStream archiveFile = ClassLoader.getSystemResourceAsStream(sZipFile);		
		CartridgesPostMultipartOptions options = new CartridgesPostMultipartOptions
				.Builder()
				.archiveFile(archiveFile)
				.build();
		
		ServiceCall<DeployCartridgeResponse> sc = acd.cartridgesPostMultipart(options);
		Response<DeployCartridgeResponse> response = sc.execute();
		DeployCartridgeResponse cartridges = response.getResult();		
		if( cartridges == null )
			return;

	}

	// PUT /cartridges
	public static void redeployCartridge(String sZipFile) throws Exception {
		
		InputStream archiveFile = ClassLoader.getSystemResourceAsStream(sZipFile);
		CartridgesPutMultipartOptions options = new CartridgesPutMultipartOptions
				.Builder()
				.archiveFile(archiveFile)
				.build();
		
		ServiceCall<DeployCartridgeResponse> sc = acd.cartridgesPutMultipart(options);
		Response<DeployCartridgeResponse> response = sc.execute();
		DeployCartridgeResponse cartridges = response.getResult();		
		if( cartridges == null )
			return;

	}

	// POST /deploy
	public static void legacyDeploy(String sZipFile) throws Exception {
		
		InputStream archiveFile = ClassLoader.getSystemResourceAsStream(sZipFile);
		DeployCartridgeOptions options = new DeployCartridgeOptions
					.Builder()
					.archiveFile(archiveFile)
					.update(true)
					.build();
		
		ServiceCall<DeployCartridgeResponse> sc = acd.deployCartridge(options);
		Response<DeployCartridgeResponse> response = sc.execute();
		DeployCartridgeResponse cartridges = response.getResult();		
		if( cartridges == null )
			return;
			
	}
	

}
