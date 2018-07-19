package org.goods.living.tech.health.device.service;

import java.io.InputStream;
import java.util.Date;
import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.codehaus.jackson.JsonNode;
import org.goods.living.tech.health.device.jpa.controllers.DataBalanceJpaController;
import org.goods.living.tech.health.device.jpa.controllers.UsersJpaController;
import org.goods.living.tech.health.device.jpa.dao.DataBalance;
import org.goods.living.tech.health.device.jpa.dao.Users;
import org.goods.living.tech.health.device.models.Result;
import org.goods.living.tech.health.device.service.security.qualifier.Secured;
import org.goods.living.tech.health.device.service.security.qualifier.UserCategory;
import org.goods.living.tech.health.device.utility.Constants;
import org.goods.living.tech.health.device.utility.JSonHelper;

import com.vladmihalcea.hibernate.type.json.internal.JacksonUtil;

//https://dzone.com/articles/lets-compare-jax-rs-vs-spring-for-rest-endpoints

@Path(Constants.URL.DATABALANCE)
@Named
@RequestScoped
public class DatabalanceService extends BaseService {

	// @Inject
	// @PersistenceUnitQualifier(PersistenceUnitEnum.POSTGRES_MANAGEMENT)
	// private EntityManagerFactory copd_management_emf;

	// @Inject
	// private ApplicationParameters applicationParameters;

	@Inject
	DataBalanceJpaController dataBalanceJpaController;

	@Inject
	UsersJpaController usersJpaController;

	// String formattedDate = dateFormat.format(date);

	public DatabalanceService() {
	}

	@Secured(value = UserCategory.USER)
	@POST
	// @Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Path(Constants.URL.CREATE)
	public Result<String> create(InputStream incomingData) throws Exception {
		logger.debug("create");
		// JSONObject response = new JSONObject(responseString);
		List<JsonNode> list = JSonHelper.getJsonNodeArray(incomingData);

		Users user = getCurrentUser();

		for (JsonNode j : list) {
			logger.debug(j);

			DataBalance model = new DataBalance();
			model.setUserId(user);
			model.setBalance(j.has("balance") ? j.get("balance").asDouble() : null);
			model.setBalanceMessage(j.has("balanceMessage") ? j.get("balanceMessage").asText() : null);
			model.setMessage(j.has("message") ? j.get("message").asText() : null);

			if (j.has("info")) {
				// JacksonUtil.toJsonNode();
				com.fasterxml.jackson.databind.JsonNode entity = JacksonUtil.toJsonNode(j.get("info").toString());
				model.setInfo(entity);
			}

			if (j.has("recordedAt")) {
				Date recordedAt = dateFormat.parse(j.get("recordedAt").asText());

				model.setRecordedAt(recordedAt);
			}

			model.setCreatedAt(new Date());
			dataBalanceJpaController.create(model);
		}

		// NodeBean toValue = mapper.convertValue(node, NodeBean.cla

		Result<String> result = new Result<String>(true, "", null);
		return result;

	}

	@POST
	// @Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Path(Constants.URL.DATABALANCE_USSDCODES)
	public Result<String> ussdCodes(InputStream incomingData) {
		logger.debug("ussdCodes");

		String ussd = applicationParameters.getUSSDBalanceCodes();

		Result<String> result = new Result<String>(true, "", ussd);
		return result;

	}

}