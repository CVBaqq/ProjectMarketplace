package com.intuit.cg.marketplace;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.intuit.cg.marketplace.model.dto.AutoBidDTO;
import com.intuit.cg.marketplace.model.dto.BidDTO;
import com.intuit.cg.marketplace.model.dto.ProjectDTO;
import com.intuit.cg.marketplace.model.dto.UserDTO;
import com.intuit.cg.marketplace.model.request.AutoBidRequest;
import com.intuit.cg.marketplace.model.request.BidRequest;
import com.intuit.cg.marketplace.model.request.ProjectRequest;
import com.intuit.cg.marketplace.model.request.UserRequest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 1. Spring offers test runner to be able to run a spring instance to be able perform integration testing with
 * the database. Thus, we have a gateway to test persistence of the data model from request to database; end-to-end.
 * Using @SpringBootTest, we can achieve this.
 *
 * 2. Perform junit testing on edge cases to catch things that may not often occur. This can potentially be things like
 * requests that are invalid or violates certain ranges or format. We can also mock up a dependent layer to setup the
 * environment in a way that will easily allow us to hit these cases. (Branch coverage)
 * Some of these cases may be...
 * 1. When someone works for free, meaning someone has a bid of 0.
 *    - This person automatically should win and additional bets should not be taken.
 * 2. When two auto bids are placed.
 *    - The result should show all the bets placed from an auto bet.
 *    - Project get request should show the current winner resulted from the auto bet.
 * 3. Validations on the bid parameters coming in.
 *    - Regular bids and auto bids should not be lower than the current bid and not higher than the project budget.
 *    - Regular bids and auto bids should not be lower than 0; 0 means the freelancer works for free.
 *    - Bidder cannot be the seller.
 *    - Bidding user and project should exist in the system.
 *
 * 3. Simulation of real time requests. This can be an integration tests where we make requests that may be similar to
 * what can occur when the service is live. This can potentially test for concurrent requests and see if the server can
 * handle it.
 */

/**
 * This is an integration tests that will test a whole bidding flow against an actual server.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class MarketplaceIntegrationTest {

  @Autowired
  private MockMvc mvc;


  @Test
  public void shouldPersistRequests() throws Exception {
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));

    UserRequest userRequest = new UserRequest();
    userRequest.setName("Ryan Sheng");

    UserDTO userResult = new UserDTO((long)1, (long)1, (long)1, userRequest.getName());

    mvc.perform(post("/user")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(userRequest)))
            .andExpect(status().isOk())
            .andExpect(content().json(objectMapper.writeValueAsString(userResult)));

    UserRequest bidderUserRequest = new UserRequest();
    bidderUserRequest.setName("Saquon Barkley");

    UserDTO bidderUserResult = new UserDTO((long)2, (long)2, (long)2, bidderUserRequest.getName());

    mvc.perform(post("/user")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(bidderUserRequest)))
            .andExpect(status().isOk())
            .andExpect(content().json(objectMapper.writeValueAsString(bidderUserResult)));

    UserRequest bidderTwoUserRequest = new UserRequest();
    bidderTwoUserRequest.setName("Intuit Sock");

    UserDTO bidderTwoUserResult = new UserDTO((long)3, (long)3, (long)3, bidderTwoUserRequest.getName());

    mvc.perform(post("/user")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(bidderTwoUserRequest)))
            .andExpect(status().isOk())
            .andExpect(content().json(objectMapper.writeValueAsString(bidderTwoUserResult)));

    Calendar calendar = Calendar.getInstance();
    calendar.setTime(new Date());
    calendar.add(Calendar.HOUR, 1);
    Date bidStop = calendar.getTime();

    ProjectRequest projectRequest = new ProjectRequest();
    projectRequest.setBidStop(bidStop);
    projectRequest.setTitle("Make my app");
    projectRequest.setDescription("whatsapp");
    projectRequest.setMaxBudget(BigDecimal.valueOf(200));
    projectRequest.setSellerId((long)1);

    ProjectDTO projectResult = new ProjectDTO((long)1, projectRequest.getSellerId(), projectRequest.getTitle(), projectRequest.getDescription(),
            BigDecimal.valueOf(200), bidStop, null, null, Collections.emptyList());
    mvc.perform(post("/projects")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(projectRequest)))
            .andExpect(status().isOk())
            .andExpect(content().json(objectMapper.writeValueAsString(projectResult)));

    BidRequest bidRequest = new BidRequest();
    bidRequest.setAmount(BigDecimal.valueOf(150));
    bidRequest.setBuyerId(bidderUserResult.getBuyerId());
    bidRequest.setProjectId(projectResult.getId());

    BidDTO firstBidExpected = new BidDTO();
    firstBidExpected.setId((long)1);
    firstBidExpected.setAmount(bidRequest.getAmount());
    firstBidExpected.setProjectId(projectResult.getId());
    firstBidExpected.setBuyerId(bidderUserResult.getBuyerId());

    String firstBidResult = mvc.perform(post("/bids")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(bidRequest)))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();
    BidDTO bidDTOResult = objectMapper.readValue(firstBidResult, BidDTO.class);
    firstBidExpected.setCreated(bidDTOResult.getCreated());
    assertEquals(firstBidExpected, bidDTOResult);

    AutoBidRequest autoBidRequest = new AutoBidRequest();
    autoBidRequest.setBuyerId(bidderTwoUserResult.getBuyerId());
    autoBidRequest.setIncrement(BigDecimal.valueOf(5));
    autoBidRequest.setTargetAmount(BigDecimal.valueOf(130));
    autoBidRequest.setProjectId(projectResult.getId());

    AutoBidDTO autoBidExpected = new AutoBidDTO();
    autoBidExpected.setProjectId(projectResult.getId());
    autoBidExpected.setTargetAmount(autoBidRequest.getTargetAmount());
    autoBidExpected.setBuyerId(autoBidRequest.getBuyerId());

    BidDTO bidExpected = new BidDTO();
    bidExpected.setBuyerId(autoBidRequest.getBuyerId());
    bidExpected.setProjectId(autoBidRequest.getProjectId());
    bidExpected.setAmount(BigDecimal.valueOf(145.00).setScale(2, BigDecimal.ROUND_HALF_UP));
    bidExpected.setAutobidId((long) 1);
    bidExpected.setId((long)2);

    String autoBidResponseString = mvc.perform(post("/bids/autobid")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(autoBidRequest)))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();
    AutoBidDTO autoBidResult = objectMapper.readValue(autoBidResponseString, AutoBidDTO.class);
    bidExpected.setCreated(autoBidResult.getCreated());
    assertEquals(bidExpected, autoBidResult.getBids().get(0));
  }
}
