/*
 * Copyright (c) 2015 Fraunhofer FOKUS
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.openbaton.autoscaling.api;

import com.google.gson.*;
import net.minidev.json.parser.JSONParser;
import org.openbaton.autoscaling.catalogue.VnfrMonitor;
import org.openbaton.autoscaling.core.ElasticityManagement;
import org.openbaton.catalogue.mano.record.NetworkServiceRecord;
import org.openbaton.catalogue.nfvo.Action;
import org.openbaton.catalogue.nfvo.ApplicationEventNFVO;
import org.openbaton.exceptions.NotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.json.GsonJsonParser;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Set;

@RestController
@RequestMapping("/event")
public class RestEvent {

	private Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private ElasticityManagement elasticityManagement;

    /**
     * Adds a new VNF software Image to the image repository
     *
     * @param msg : NSR in payload to add for autoscaling
     */
    @RequestMapping(value = "", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public void create(@RequestBody String msg) throws NotFoundException {
        log.debug("========================");
        log.debug("msg=" + msg);
        JsonParser jsonParser = new JsonParser();
        JsonObject json = jsonParser.parse(msg).getAsJsonObject();
        Gson mapper = new GsonBuilder().create();
        Action action = mapper.fromJson(json.get("action"), Action.class);
        log.debug("ACTION=" + action);
        NetworkServiceRecord nsr = mapper.fromJson(json.get("payload"), NetworkServiceRecord.class);
        log.debug("NSR=" + nsr);
        elasticityManagement.activate(nsr);
    }

//    /**
//     * Adds a new VNF software Image to the image repository
//     *
//     * @param applicationEventNFVO : NSR to add for autoscaling
//     */
//    @RequestMapping(value = "", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
//    @ResponseStatus(HttpStatus.CREATED)
//    public void create(@RequestBody ApplicationEventNFVO applicationEventNFVO) throws NotFoundException {
//        log.debug("========================");
//        log.debug("nsr=" + (NetworkServiceRecord) applicationEventNFVO.getPayload());
//    }

    /**
     * Removes the Application from the Application repository
     *
     * @param nsrId : The nsr's id to be deleted from autoscaling
     */
    @RequestMapping(value = "{nsrId}", method = RequestMethod.DELETE)
    @ResponseStatus(HttpStatus.OK)
    public void delete(@PathVariable("nsrId") NetworkServiceRecord nsr) throws NotFoundException {
        log.debug("========================");
        log.debug("nsr=" + nsr);
        elasticityManagement.deactivate(nsr);
    }
}