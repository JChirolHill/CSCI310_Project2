package projects.chirolhill.juliette.csci310_project2.model;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;

import projects.chirolhill.juliette.csci310_project2.MapsActivity;

import static org.junit.Assert.*;

public class DirectionsFetcherTest {

    private DirectionsFetcher directionsFetcher;
    private final String APIResponseWalking = "{\n" +
            "   \"geocoded_waypoints\" : [\n" +
            "      {\n" +
            "         \"geocoder_status\" : \"OK\",\n" +
            "         \"place_id\" : \"ChIJvR_pq5zHwoARNEw35wfCi2Y\",\n" +
            "         \"types\" : [ \"premise\" ]\n" +
            "      },\n" +
            "      {\n" +
            "         \"geocoder_status\" : \"OK\",\n" +
            "         \"place_id\" : \"ChIJ5ZZvA-XHwoARaDppN-997dk\",\n" +
            "         \"types\" : [ \"street_address\" ]\n" +
            "      }\n" +
            "   ],\n" +
            "   \"routes\" : [\n" +
            "      {\n" +
            "         \"bounds\" : {\n" +
            "            \"northeast\" : {\n" +
            "               \"lat\" : 34.0521493,\n" +
            "               \"lng\" : -118.2839997\n" +
            "            },\n" +
            "            \"southwest\" : {\n" +
            "               \"lat\" : 34.0249875,\n" +
            "               \"lng\" : -118.2877037\n" +
            "            }\n" +
            "         },\n" +
            "         \"copyrights\" : \"Map data ©2019\",\n" +
            "         \"legs\" : [\n" +
            "            {\n" +
            "               \"distance\" : {\n" +
            "                  \"text\" : \"2.1 mi\",\n" +
            "                  \"value\" : 3455\n" +
            "               },\n" +
            "               \"duration\" : {\n" +
            "                  \"text\" : \"43 mins\",\n" +
            "                  \"value\" : 2603\n" +
            "               },\n" +
            "               \"end_address\" : \"925 W Jefferson Blvd, Los Angeles, CA 90007, USA\",\n" +
            "               \"end_location\" : {\n" +
            "                  \"lat\" : 34.025278,\n" +
            "                  \"lng\" : -118.285494\n" +
            "               },\n" +
            "               \"start_address\" : \"2580 W Olympic Blvd, Los Angeles, CA 90006, USA\",\n" +
            "               \"start_location\" : {\n" +
            "                  \"lat\" : 34.0521493,\n" +
            "                  \"lng\" : -118.2873903\n" +
            "               },\n" +
            "               \"steps\" : [\n" +
            "                  {\n" +
            "                     \"distance\" : {\n" +
            "                        \"text\" : \"85 ft\",\n" +
            "                        \"value\" : 26\n" +
            "                     },\n" +
            "                     \"duration\" : {\n" +
            "                        \"text\" : \"1 min\",\n" +
            "                        \"value\" : 19\n" +
            "                     },\n" +
            "                     \"end_location\" : {\n" +
            "                        \"lat\" : 34.0520124,\n" +
            "                        \"lng\" : -118.2875233\n" +
            "                     },\n" +
            "                     \"html_instructions\" : \"Head \\u003cb\\u003ewest\\u003c/b\\u003e toward \\u003cb\\u003eElden Ave\\u003c/b\\u003e\",\n" +
            "                     \"polyline\" : {\n" +
            "                        \"points\" : \"}xynEd_~pU?T?@@?B@T?\"\n" +
            "                     },\n" +
            "                     \"start_location\" : {\n" +
            "                        \"lat\" : 34.0521493,\n" +
            "                        \"lng\" : -118.2873903\n" +
            "                     },\n" +
            "                     \"travel_mode\" : \"WALKING\"\n" +
            "                  },\n" +
            "                  {\n" +
            "                     \"distance\" : {\n" +
            "                        \"text\" : \"69 ft\",\n" +
            "                        \"value\" : 21\n" +
            "                     },\n" +
            "                     \"duration\" : {\n" +
            "                        \"text\" : \"1 min\",\n" +
            "                        \"value\" : 15\n" +
            "                     },\n" +
            "                     \"end_location\" : {\n" +
            "                        \"lat\" : 34.0520109,\n" +
            "                        \"lng\" : -118.2877037\n" +
            "                     },\n" +
            "                     \"html_instructions\" : \"Turn \\u003cb\\u003eright\\u003c/b\\u003e toward \\u003cb\\u003eElden Ave\\u003c/b\\u003e\",\n" +
            "                     \"maneuver\" : \"turn-right\",\n" +
            "                     \"polyline\" : {\n" +
            "                        \"points\" : \"axynE~_~pU?b@\"\n" +
            "                     },\n" +
            "                     \"start_location\" : {\n" +
            "                        \"lat\" : 34.0520124,\n" +
            "                        \"lng\" : -118.2875233\n" +
            "                     },\n" +
            "                     \"travel_mode\" : \"WALKING\"\n" +
            "                  },\n" +
            "                  {\n" +
            "                     \"distance\" : {\n" +
            "                        \"text\" : \"0.3 mi\",\n" +
            "                        \"value\" : 526\n" +
            "                     },\n" +
            "                     \"duration\" : {\n" +
            "                        \"text\" : \"6 mins\",\n" +
            "                        \"value\" : 388\n" +
            "                     },\n" +
            "                     \"end_location\" : {\n" +
            "                        \"lat\" : 34.0473156,\n" +
            "                        \"lng\" : -118.2876996\n" +
            "                     },\n" +
            "                     \"html_instructions\" : \"Turn \\u003cb\\u003eleft\\u003c/b\\u003e onto \\u003cb\\u003eElden Ave\\u003c/b\\u003e\",\n" +
            "                     \"maneuver\" : \"turn-left\",\n" +
            "                     \"polyline\" : {\n" +
            "                        \"points\" : \"axynEba~pUhC?`B?L?~HEL?fFAdA?\\\\?NFD?\"\n" +
            "                     },\n" +
            "                     \"start_location\" : {\n" +
            "                        \"lat\" : 34.0520109,\n" +
            "                        \"lng\" : -118.2877037\n" +
            "                     },\n" +
            "                     \"travel_mode\" : \"WALKING\"\n" +
            "                  },\n" +
            "                  {\n" +
            "                     \"distance\" : {\n" +
            "                        \"text\" : \"390 ft\",\n" +
            "                        \"value\" : 119\n" +
            "                     },\n" +
            "                     \"duration\" : {\n" +
            "                        \"text\" : \"1 min\",\n" +
            "                        \"value\" : 87\n" +
            "                     },\n" +
            "                     \"end_location\" : {\n" +
            "                        \"lat\" : 34.0472487,\n" +
            "                        \"lng\" : -118.2863684\n" +
            "                     },\n" +
            "                     \"html_instructions\" : \"Turn \\u003cb\\u003eleft\\u003c/b\\u003e onto \\u003cb\\u003eW Pico Blvd\\u003c/b\\u003e\",\n" +
            "                     \"maneuver\" : \"turn-left\",\n" +
            "                     \"polyline\" : {\n" +
            "                        \"points\" : \"wzxnEba~pU?kA?_@?Q?mB?U?GL?\"\n" +
            "                     },\n" +
            "                     \"start_location\" : {\n" +
            "                        \"lat\" : 34.0473156,\n" +
            "                        \"lng\" : -118.2876996\n" +
            "                     },\n" +
            "                     \"travel_mode\" : \"WALKING\"\n" +
            "                  },\n" +
            "                  {\n" +
            "                     \"distance\" : {\n" +
            "                        \"text\" : \"0.2 mi\",\n" +
            "                        \"value\" : 338\n" +
            "                     },\n" +
            "                     \"duration\" : {\n" +
            "                        \"text\" : \"4 mins\",\n" +
            "                        \"value\" : 257\n" +
            "                     },\n" +
            "                     \"end_location\" : {\n" +
            "                        \"lat\" : 34.0442788,\n" +
            "                        \"lng\" : -118.2863503\n" +
            "                     },\n" +
            "                     \"html_instructions\" : \"Turn \\u003cb\\u003eright\\u003c/b\\u003e onto \\u003cb\\u003eMagnolia Ave\\u003c/b\\u003e\",\n" +
            "                     \"maneuver\" : \"turn-right\",\n" +
            "                     \"polyline\" : {\n" +
            "                        \"points\" : \"izxnExx}pUt@?h@AdB?fEAfA?zB?\"\n" +
            "                     },\n" +
            "                     \"start_location\" : {\n" +
            "                        \"lat\" : 34.0472487,\n" +
            "                        \"lng\" : -118.2863684\n" +
            "                     },\n" +
            "                     \"travel_mode\" : \"WALKING\"\n" +
            "                  },\n" +
            "                  {\n" +
            "                     \"distance\" : {\n" +
            "                        \"text\" : \"0.1 mi\",\n" +
            "                        \"value\" : 212\n" +
            "                     },\n" +
            "                     \"duration\" : {\n" +
            "                        \"text\" : \"2 mins\",\n" +
            "                        \"value\" : 145\n" +
            "                     },\n" +
            "                     \"end_location\" : {\n" +
            "                        \"lat\" : 34.0437199,\n" +
            "                        \"lng\" : -118.2841184\n" +
            "                     },\n" +
            "                     \"html_instructions\" : \"Turn \\u003cb\\u003eleft\\u003c/b\\u003e onto \\u003cb\\u003eVenice Blvd\\u003c/b\\u003e\",\n" +
            "                     \"maneuver\" : \"turn-left\",\n" +
            "                     \"polyline\" : {\n" +
            "                        \"points\" : \"wgxnEtx}pUHcABQBO?CPqABQPmAHi@BSHe@Hk@BQN?\"\n" +
            "                     },\n" +
            "                     \"start_location\" : {\n" +
            "                        \"lat\" : 34.0442788,\n" +
            "                        \"lng\" : -118.2863503\n" +
            "                     },\n" +
            "                     \"travel_mode\" : \"WALKING\"\n" +
            "                  },\n" +
            "                  {\n" +
            "                     \"distance\" : {\n" +
            "                        \"text\" : \"1.3 mi\",\n" +
            "                        \"value\" : 2039\n" +
            "                     },\n" +
            "                     \"duration\" : {\n" +
            "                        \"text\" : \"26 mins\",\n" +
            "                        \"value\" : 1568\n" +
            "                     },\n" +
            "                     \"end_location\" : {\n" +
            "                        \"lat\" : 34.0254284,\n" +
            "                        \"lng\" : -118.2839997\n" +
            "                     },\n" +
            "                     \"html_instructions\" : \"Turn \\u003cb\\u003eright\\u003c/b\\u003e onto \\u003cb\\u003eS Hoover St\\u003c/b\\u003e\",\n" +
            "                     \"maneuver\" : \"turn-right\",\n" +
            "                     \"polyline\" : {\n" +
            "                        \"points\" : \"gdxnEvj}pUl@?P?PAN?D?h@?X?P?R?hAAL?X?J?L?V?N?|DAL?L?`@?f@?n@?d@AT??PP?D?n@ATAh@A`@A\\\\ALAf@AXAd@CL?V@nACb@?^?D?V?D?F?B?R@D?@?D?H?d@?D?L?\\\\?d@?T?L?L?NAb@?f@?L?D?T?L?`AAnA?L?J?b@?j@?\\\\AX@L?dA?ZAL?nC?bB?N?tCB^AJ?lAAN?TAb@?H?hBAN?^?X?|@?v@?N?bA?rB?L?t@@d@?R?l@?`A?tA?N?b@CL?HA\"\n" +
            "                     },\n" +
            "                     \"start_location\" : {\n" +
            "                        \"lat\" : 34.0437199,\n" +
            "                        \"lng\" : -118.2841184\n" +
            "                     },\n" +
            "                     \"travel_mode\" : \"WALKING\"\n" +
            "                  },\n" +
            "                  {\n" +
            "                     \"distance\" : {\n" +
            "                        \"text\" : \"344 ft\",\n" +
            "                        \"value\" : 105\n" +
            "                     },\n" +
            "                     \"duration\" : {\n" +
            "                        \"text\" : \"1 min\",\n" +
            "                        \"value\" : 75\n" +
            "                     },\n" +
            "                     \"end_location\" : {\n" +
            "                        \"lat\" : 34.0249875,\n" +
            "                        \"lng\" : -118.284824\n" +
            "                     },\n" +
            "                     \"html_instructions\" : \"Turn \\u003cb\\u003eright\\u003c/b\\u003e\",\n" +
            "                     \"maneuver\" : \"turn-right\",\n" +
            "                     \"polyline\" : {\n" +
            "                        \"points\" : \"}qtnE~i}pUBHBRD^@N@NBHBFBDHFn@d@\"\n" +
            "                     },\n" +
            "                     \"start_location\" : {\n" +
            "                        \"lat\" : 34.0254284,\n" +
            "                        \"lng\" : -118.2839997\n" +
            "                     },\n" +
            "                     \"travel_mode\" : \"WALKING\"\n" +
            "                  },\n" +
            "                  {\n" +
            "                     \"distance\" : {\n" +
            "                        \"text\" : \"226 ft\",\n" +
            "                        \"value\" : 69\n" +
            "                     },\n" +
            "                     \"duration\" : {\n" +
            "                        \"text\" : \"1 min\",\n" +
            "                        \"value\" : 49\n" +
            "                     },\n" +
            "                     \"end_location\" : {\n" +
            "                        \"lat\" : 34.025278,\n" +
            "                        \"lng\" : -118.285494\n" +
            "                     },\n" +
            "                     \"html_instructions\" : \"Turn \\u003cb\\u003eright\\u003c/b\\u003e\\u003cdiv style=\\\"font-size:0.9em\\\"\\u003eRestricted usage road\\u003c/div\\u003e\\u003cdiv style=\\\"font-size:0.9em\\\"\\u003eDestination will be on the right\\u003c/div\\u003e\",\n" +
            "                     \"maneuver\" : \"turn-right\",\n" +
            "                     \"polyline\" : {\n" +
            "                        \"points\" : \"eotnEbo}pUy@dC\"\n" +
            "                     },\n" +
            "                     \"start_location\" : {\n" +
            "                        \"lat\" : 34.0249875,\n" +
            "                        \"lng\" : -118.284824\n" +
            "                     },\n" +
            "                     \"travel_mode\" : \"WALKING\"\n" +
            "                  }\n" +
            "               ],\n" +
            "               \"traffic_speed_entry\" : [],\n" +
            "               \"via_waypoint\" : []\n" +
            "            }\n" +
            "         ],\n" +
            "         \"overview_polyline\" : {\n" +
            "            \"points\" : \"}xynEd_~pU@VX@?b@hC?nB?lIElHA\\\\?NFD??kA?q@?cC?GL?~AAlHAbE?LuAXwBh@qDL}@|@?x@AfFAxIAtAAT??PV?dACvBGtBGvEAtA@nLCtF?|GApE@zHEzK@hD?vCCHABHHr@Jp@LLn@d@y@dC\"\n" +
            "         },\n" +
            "         \"summary\" : \"S Hoover St\",\n" +
            "         \"warnings\" : [\n" +
            "            \"Walking directions are in beta. Use caution – This route may be missing sidewalks or pedestrian paths.\"\n" +
            "         ],\n" +
            "         \"waypoint_order\" : []\n" +
            "      }\n" +
            "   ],\n" +
            "   \"status\" : \"OK\"\n" +
            "}";
    private final String APIResponseDriving = "{\n" +
            "   \"geocoded_waypoints\" : [\n" +
            "      {\n" +
            "         \"geocoder_status\" : \"OK\",\n" +
            "         \"place_id\" : \"ChIJvR_pq5zHwoARNEw35wfCi2Y\",\n" +
            "         \"types\" : [ \"premise\" ]\n" +
            "      },\n" +
            "      {\n" +
            "         \"geocoder_status\" : \"OK\",\n" +
            "         \"place_id\" : \"ChIJ5ZZvA-XHwoARaDppN-997dk\",\n" +
            "         \"types\" : [ \"street_address\" ]\n" +
            "      }\n" +
            "   ],\n" +
            "   \"routes\" : [\n" +
            "      {\n" +
            "         \"bounds\" : {\n" +
            "            \"northeast\" : {\n" +
            "               \"lat\" : 34.0524137,\n" +
            "               \"lng\" : -118.2839763\n" +
            "            },\n" +
            "            \"southwest\" : {\n" +
            "               \"lat\" : 34.0259547,\n" +
            "               \"lng\" : -118.2873903\n" +
            "            }\n" +
            "         },\n" +
            "         \"copyrights\" : \"Map data ©2019\",\n" +
            "         \"legs\" : [\n" +
            "            {\n" +
            "               \"distance\" : {\n" +
            "                  \"text\" : \"2.1 mi\",\n" +
            "                  \"value\" : 3383\n" +
            "               },\n" +
            "               \"duration\" : {\n" +
            "                  \"text\" : \"10 mins\",\n" +
            "                  \"value\" : 584\n" +
            "               },\n" +
            "               \"end_address\" : \"925 W Jefferson Blvd, Los Angeles, CA 90007, USA\",\n" +
            "               \"end_location\" : {\n" +
            "                  \"lat\" : 34.0259547,\n" +
            "                  \"lng\" : -118.2853772\n" +
            "               },\n" +
            "               \"start_address\" : \"2580 W Olympic Blvd, Los Angeles, CA 90006, USA\",\n" +
            "               \"start_location\" : {\n" +
            "                  \"lat\" : 34.0521493,\n" +
            "                  \"lng\" : -118.2873903\n" +
            "               },\n" +
            "               \"steps\" : [\n" +
            "                  {\n" +
            "                     \"distance\" : {\n" +
            "                        \"text\" : \"43 ft\",\n" +
            "                        \"value\" : 13\n" +
            "                     },\n" +
            "                     \"duration\" : {\n" +
            "                        \"text\" : \"1 min\",\n" +
            "                        \"value\" : 2\n" +
            "                     },\n" +
            "                     \"end_location\" : {\n" +
            "                        \"lat\" : 34.0521486,\n" +
            "                        \"lng\" : -118.2872499\n" +
            "                     },\n" +
            "                     \"html_instructions\" : \"Head \\u003cb\\u003eeast\\u003c/b\\u003e toward \\u003cb\\u003eW Olympic Blvd\\u003c/b\\u003e\",\n" +
            "                     \"polyline\" : {\n" +
            "                        \"points\" : \"}xynEd_~pU?[\"\n" +
            "                     },\n" +
            "                     \"start_location\" : {\n" +
            "                        \"lat\" : 34.0521493,\n" +
            "                        \"lng\" : -118.2873903\n" +
            "                     },\n" +
            "                     \"travel_mode\" : \"DRIVING\"\n" +
            "                  },\n" +
            "                  {\n" +
            "                     \"distance\" : {\n" +
            "                        \"text\" : \"92 ft\",\n" +
            "                        \"value\" : 28\n" +
            "                     },\n" +
            "                     \"duration\" : {\n" +
            "                        \"text\" : \"1 min\",\n" +
            "                        \"value\" : 14\n" +
            "                     },\n" +
            "                     \"end_location\" : {\n" +
            "                        \"lat\" : 34.0524034,\n" +
            "                        \"lng\" : -118.2872552\n" +
            "                     },\n" +
            "                     \"html_instructions\" : \"Turn \\u003cb\\u003eleft\\u003c/b\\u003e toward \\u003cb\\u003eW Olympic Blvd\\u003c/b\\u003e\",\n" +
            "                     \"maneuver\" : \"turn-left\",\n" +
            "                     \"polyline\" : {\n" +
            "                        \"points\" : \"}xynEh~}pUq@@\"\n" +
            "                     },\n" +
            "                     \"start_location\" : {\n" +
            "                        \"lat\" : 34.0521486,\n" +
            "                        \"lng\" : -118.2872499\n" +
            "                     },\n" +
            "                     \"travel_mode\" : \"DRIVING\"\n" +
            "                  },\n" +
            "                  {\n" +
            "                     \"distance\" : {\n" +
            "                        \"text\" : \"0.2 mi\",\n" +
            "                        \"value\" : 280\n" +
            "                     },\n" +
            "                     \"duration\" : {\n" +
            "                        \"text\" : \"1 min\",\n" +
            "                        \"value\" : 51\n" +
            "                     },\n" +
            "                     \"end_location\" : {\n" +
            "                        \"lat\" : 34.0524044,\n" +
            "                        \"lng\" : -118.2842179\n" +
            "                     },\n" +
            "                     \"html_instructions\" : \"Turn \\u003cb\\u003eright\\u003c/b\\u003e onto \\u003cb\\u003eW Olympic Blvd\\u003c/b\\u003e\",\n" +
            "                     \"maneuver\" : \"turn-right\",\n" +
            "                     \"polyline\" : {\n" +
            "                        \"points\" : \"ozynEj~}pU?UAm@?aA?KAgB@yBAeA@U@oC\"\n" +
            "                     },\n" +
            "                     \"start_location\" : {\n" +
            "                        \"lat\" : 34.0524034,\n" +
            "                        \"lng\" : -118.2872552\n" +
            "                     },\n" +
            "                     \"travel_mode\" : \"DRIVING\"\n" +
            "                  },\n" +
            "                  {\n" +
            "                     \"distance\" : {\n" +
            "                        \"text\" : \"1.8 mi\",\n" +
            "                        \"value\" : 2874\n" +
            "                     },\n" +
            "                     \"duration\" : {\n" +
            "                        \"text\" : \"7 mins\",\n" +
            "                        \"value\" : 449\n" +
            "                     },\n" +
            "                     \"end_location\" : {\n" +
            "                        \"lat\" : 34.0265696,\n" +
            "                        \"lng\" : -118.2839862\n" +
            "                     },\n" +
            "                     \"html_instructions\" : \"Turn \\u003cb\\u003eright\\u003c/b\\u003e onto \\u003cb\\u003eS Hoover St\\u003c/b\\u003e\",\n" +
            "                     \"maneuver\" : \"turn-right\",\n" +
            "                     \"polyline\" : {\n" +
            "                        \"points\" : \"ozynEjk}pUp@Ab@?V?L?R?Z?V?LAV?rA?`C?r@AZ?vA?d@?xGAp@Fd@DdA?L?R?jA?fA?B?f@?ZAb@?`@A`@Ar@CXA^?^Ab@A~AAv@?P?V?N?h@?\\\\?`@AnA?`@AP?b@?hE?RAh@@f@?n@Ad@?F?^?DAn@?RAh@Cb@AZAN?f@CX?l@C\\\\?nAAb@?`@?B?V?F?D?B?R?D?@?D?H@d@?J?d@?d@AZ?T?N?b@?n@?J?\\\\?fAAtA?R?b@?j@?\\\\A`@?jA?`@?vCAnB?xCB^?J?tAC\\\\?`@AJ?pB?f@AX@|@?~@?jA?zB?z@?f@@P?l@?\"\n" +
            "                     },\n" +
            "                     \"start_location\" : {\n" +
            "                        \"lat\" : 34.0524044,\n" +
            "                        \"lng\" : -118.2842179\n" +
            "                     },\n" +
            "                     \"travel_mode\" : \"DRIVING\"\n" +
            "                  },\n" +
            "                  {\n" +
            "                     \"distance\" : {\n" +
            "                        \"text\" : \"0.1 mi\",\n" +
            "                        \"value\" : 188\n" +
            "                     },\n" +
            "                     \"duration\" : {\n" +
            "                        \"text\" : \"1 min\",\n" +
            "                        \"value\" : 68\n" +
            "                     },\n" +
            "                     \"end_location\" : {\n" +
            "                        \"lat\" : 34.0259547,\n" +
            "                        \"lng\" : -118.2853772\n" +
            "                     },\n" +
            "                     \"html_instructions\" : \"Turn \\u003cb\\u003eright\\u003c/b\\u003e\\u003cdiv style=\\\"font-size:0.9em\\\"\\u003eRestricted usage road\\u003c/div\\u003e\\u003cdiv style=\\\"font-size:0.9em\\\"\\u003eDestination will be on the left\\u003c/div\\u003e\",\n" +
            "                     \"maneuver\" : \"turn-right\",\n" +
            "                     \"polyline\" : {\n" +
            "                        \"points\" : \"aytnE|i}pUA~AAT?TAb@BRBJBFB@DBD?P?f@AV?B?@@DDBD@F@b@\"\n" +
            "                     },\n" +
            "                     \"start_location\" : {\n" +
            "                        \"lat\" : 34.0265696,\n" +
            "                        \"lng\" : -118.2839862\n" +
            "                     },\n" +
            "                     \"travel_mode\" : \"DRIVING\"\n" +
            "                  }\n" +
            "               ],\n" +
            "               \"traffic_speed_entry\" : [],\n" +
            "               \"via_waypoint\" : []\n" +
            "            }\n" +
            "         ],\n" +
            "         \"overview_polyline\" : {\n" +
            "            \"points\" : \"}xynEd_~pU?[q@@AcA?oH?{A@oCp@Az@?`@?xAAdHA|B?xGAp@FjBD`@?~D?bCElCGlECtGCpLAbFKlCGxCAjA?xD?dYExDB`BC|DAjN@~@?CtBAx@F^FHJBtAAFFDL@b@\"\n" +
            "         },\n" +
            "         \"summary\" : \"S Hoover St\",\n" +
            "         \"warnings\" : [],\n" +
            "         \"waypoint_order\" : []\n" +
            "      }\n" +
            "   ],\n" +
            "   \"status\" : \"OK\"\n" +
            "}";

    @Before
    public void setUp() throws Exception {
        // NOTE: we will not be testing the activity-dependent parts of the fetcher class, so null should be fine
        this.directionsFetcher = new DirectionsFetcher(null, null);
    }

    @Test
    public void parse() {
        StringBuilder APIResponseDrivingString = new StringBuilder();
        APIResponseDrivingString.append(APIResponseDriving);
        try {
            this.directionsFetcher.parse(new JSONObject(APIResponseDrivingString.toString()));
        } catch (JSONException jse) {
            jse.printStackTrace();
        }
        assertNotNull(this.directionsFetcher.getResponse());
    }

    @Test
    public void getResponse() {

    }
}