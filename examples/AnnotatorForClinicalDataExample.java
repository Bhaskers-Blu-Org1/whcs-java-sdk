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

 import java.io.File;
 import java.io.FileNotFoundException;
 import java.util.ArrayList;
 import java.util.Arrays;
 import java.util.List;
 import java.util.Map;

 import com.ibm.cloud.sdk.core.http.Response;
 import com.ibm.cloud.sdk.core.http.ServiceCall;
 import com.ibm.cloud.sdk.core.security.Authenticator;
 import com.ibm.cloud.sdk.core.security.BasicAuthenticator;
 import com.ibm.watson.health.acd.v1.AnnotatorForClinicalData;
 import com.ibm.watson.health.acd.v1.model.AcdProfile;
 import com.ibm.watson.health.acd.v1.model.Annotator;
 import com.ibm.watson.health.acd.v1.model.Annotator.Name;
 import com.ibm.watson.health.acd.v1.model.Concept;
 import com.ibm.watson.health.acd.v1.model.ContainerGroup;
 import com.ibm.watson.health.acd.v1.model.DeleteUserSpecificArtifactsOptions;
 import com.ibm.watson.health.acd.v1.model.DeployCartridgeOptions;
 import com.ibm.watson.health.acd.v1.model.DeployCartridgeResponse;
 import com.ibm.watson.health.acd.v1.model.Flow;
 import com.ibm.watson.health.acd.v1.model.GetProfilesOptions;
 import com.ibm.watson.health.acd.v1.util.FlowUtil;

/**
 * Sample ACD Java SDK Calls
 */
public class AnnotatorForClinicalDataExample {

	public static String version = "2020-05-18";
	public static String ACD_APIKEY="<YOUR_APIKEY_HERE>";
	public static String ACD_URL="https://us-east.wh-acd.cloud.ibm.com/wh-acd/api";
	public static String name="AnnotatorForClinicalData";
	public static String Constants_PROFILE_ID  = "p51c";
	public static String Constants_FLOW_ID  = "flow51asdf";
	public static AnnotatorForClinicalData acd = null;
	public static Annotator annotatorSC = null;

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

	}

	public static void main(String[] args) {

		analyze();
		//getProfiles();

	}

	/*
	 * Annotator for Clinical Data Text Analysis Calls
	 */

	public static void analyze() {

		List<String> annotators = new ArrayList<String>(
				Arrays.asList(
						Name.CONCEPT_DETECTION,
						Name.NEGATION));

		Flow flow = new FlowUtil.Builder().annotators(annotators).build();

		String text = "Patient has lung cancer, but did not smoke. She may consider chemotherapy as part of a treatment plan.";

		ContainerGroup response = acd.analyze(text, flow);

		List<Concept> concepts = response.getConcepts();

		for (Concept c: concepts)
			System.out.println("Type: " + c.getType() + " Name: " + c.getPreferredName());

	}

	// POST /analyze/{flowId}
	public static void analyzeWithFlow() {

		String flowId = "wh_acd.ibm_clinical_insights_v1.0_standard_flow";
		String text = "Patient has lung cancer, but did not smoke. She may consider chemotherapy as part of a treatment plan.";

		ContainerGroup response = acd.analyzeWithFlow(flowId, text);

		List<Concept> concepts = response.getConcepts();

		for (Concept c: concepts)
			System.out.println("Type: " + c.getType() + " Name: " + c.getPreferredName());

	}

	/*
	 * Profile Crud Operations
	 */

	// GET /profiles
	public static void getProfiles() {

		GetProfilesOptions options = new GetProfilesOptions.Builder().build();

		ServiceCall<Map<String, AcdProfile>> sc = acd.getProfiles(options);

		Response<Map<String, AcdProfile>> response = sc.execute();

		System.out.println(sc);

	}

	// GET /profiles/{id}
	public static void getProfile() {

	}

	// POST /profiles
	public static void createProfile() {

	}

	// PUT /profiles
	public static void updateProfile() {

	}

	// DELETE /profiles/{id}
	public static void deleteProfile() {

	}

	/*
	 * Annotator Flow Crud Operations
	 */

	// GET /flows
	public static void getFlows() {

	}

	// GET /flows/{id}
	public static void getFlow() {

	}

	// POST /flows
	public static void createFlow() {

	}

	// PUT /flows
	public static void updateFlow() {

	}

	// DELETE /flows/{id}
	public static void deleteFlow() {

	}

	// DELETE /user_data
	public static void deleteTenantData() {

		DeleteUserSpecificArtifactsOptions opts = new DeleteUserSpecificArtifactsOptions();
		Response<Void> resp = acd.deleteUserSpecificArtifacts(opts).execute();
		System.out.println(resp.getStatusCode());

	}

	// GET /status/health_check
	public static void getHealthCheck() {

	}

	// GET /cartridges
	public static void getCartridges() {

	}

	// GET /cartridges/{id}
	public static void getCartridge() {

	}

	// POST /cartridges
	public static void deployCartridge() {

	}

	// PUT /cartridges
	public static void redeployCartridge() {

	}

	// POST /deploy
	public static void legacyDeploy() {

		File cartridgeZip = new File("/tmp/my_cartridge_v1.0.zip");
		DeployCartridgeOptions opts;
		try {
			opts = new DeployCartridgeOptions.Builder()
					.archiveFile(cartridgeZip)
					.archiveFileContentType("application/octet-stream")
					.update(true).build();
			Response<DeployCartridgeResponse> r = acd.deployCartridge(opts).execute();
			System.out.println(r.getStatusCode());
		} catch (FileNotFoundException e) {
			// handle exception
		}

	}

}
