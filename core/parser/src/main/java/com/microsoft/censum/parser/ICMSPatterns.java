// Copyright (c) Microsoft Corporation.
// Licensed under the MIT License.
package com.microsoft.censum.parser;

/**
 * Patterns for the incremental Concurrent Mark and Sweep (iCMS) collector
 */
public interface ICMSPatterns extends CMSPatterns {

    String ICMS_DC = " icms_dc=(\\d+) ";

    /***** iCMS records ******/
    //Greedy: 263204.684: [GC263204.684: [ParNew (promotion failed): 153342K->153343K(153344K), 0.0910130 secs]263204.776: [CMS: 1752518K->1636004K(1926784K), 8.9443330 secs] 1889511K->1636004K(2080128K), [CMS Perm : 137004K->135442K(233916K)] icms_dc=75 , 9.0355990 secs]
    //Greedy: 410971.012: [GC 410971.012: [ParNew (promotion failed): 153068K->153068K(153344K), 0.1683040 secs]410971.180: [CMS: 1414041K->1301153K(1926784K), 7.3235580 secs] 1543745K->1301153K(2080128K), [CMS Perm : 137698K->135751K(235644K)] icms_dc=19 , 7.4921120 secs]
    //Missed: 357305.590: [GC 357305.590: [ParNew (promotion failed): 10973K->3147K(153344K), 0.0295730 secs] 1763339K->1763817K(2080128K) icms_dc=32 , 0.0297680 secs]

    //9.432: [GC 9.432: [ParNew: 145455K->17024K(153344K), 0.0183600 secs] 147142K->18720K(2080128K) icms_dc=5 , 0.0184240 secs]
    //0.042: [GC 0.042: [ParNew: 153313K->17024K(153344K), 0.1259050 secs] 1431172K->1334562K(2080128K) icms_dc=23 , 0.1261340 secs]
    //2015-07-02T10:50:17.491+0100: 494057.529: [GC2015-07-02T10:50:17.491+0100: 494057.530: [ParNew: 553019K->9143K(613440K), 0.1222170 secs] 10355626K->9812666K(16709120K) icms_dc=0 , 0.1226510 secs] [Times: user=0.83 sys=0.01, real=0.12 secs]
    //2015-06-26T17:40:28.063+0100: 268.102: [GC2015-06-26T17:40:28.064+0100: 268.102: [ParNew: 547627K->28112K(613440K), 0.0768200 secs] 547627K->28112K(16709120K) icms_dc=5 , 0.0771220 secs] [Times: user=0.40 sys=0.07, real=0.08 secs]
    GCParseRule iCMS_PARNEW = new GCParseRule("iCMS_PARNEW", GC_PREFIX + PARNEW_BLOCK + " " + BEFORE_AFTER_CONFIGURED + ICMS_DC + ", " + PAUSE_TIME);

    //2016-08-17T10:41:41.088-0500: 76388.020: [GC (Allocation Failure) 2016-08-17T10:41:41.088-0500: 76388.020: [ParNew (promotion failed): 428321K->431492K(471872K), 0.1049894 secs]2016-08-17T10:41:41.193-0500: 76388.125: [CMS: 466697K->186596K(524288K), 0.7357085 secs] 893715K->186596K(996160K), [Metaspace: 46524K->46524K(1097728K)] icms_dc=0 , 0.8408171 secs] [Times: user=0.91 sys=0.00, real=0.84 secs]
    GCParseRule iCMS_PARNEW_PROMOTION_FAILURE = new GCParseRule("iCMS_PARNEW_PROMOTION_FAILURE", GC_PREFIX + PARNEW_PROMOTION_FAILED_BLOCK + CMS_TENURED_BLOCK + " " + BEFORE_AFTER_CONFIGURED + ", " + PERM_RECORD + ICMS_DC + ", " + PAUSE_TIME);
    GCParseRule iCMS_PARNEW_PROMOTION_FAILURE_RECORD = new GCParseRule("iCMS_PARNEW_PROMOTION_FAILURE_RECORD", GC_PREFIX + PARNEW_WITH_PROMOTION_FAILURE_SIZE_BLOCK + CMS_TENURED_BLOCK + " " + BEFORE_AFTER_CONFIGURED + ", " + PERM_RECORD + ICMS_DC + ", " + PAUSE_TIME);

    //: 81792K->0K(81856K), 0.0431036 secs] 90187K->11671K(2097088K) icms_dc=5 , 0.0435503 secs]
    GCParseRule iCMS_PARNEW_DEFNEW_TENURING_DETAILS = new GCParseRule("iCMS_PARNEW_DEFNEW_TENURING_DETAILS", "^: " + BEFORE_AFTER_CONFIGURED_PAUSE + "\\] " + BEFORE_AFTER_CONFIGURED + ICMS_DC + ", " + PAUSE_TIME + "\\]");

    /* Full collections */

    //2013-06-06T14:12:49.554+0200: 534744,148: [Full GC (System) 534744,148: [CMS: 1513767K->410320K(3512768K), 2,1361260 secs] 1598422K->410320K(4126208K), [CMS Perm : 120074K->119963K(200424K)] icms_dc=0 , 2,1363550 secs]
    //619930,816: [Full GC 619930,816: [CMS: 3512768K->3512767K(3512768K), 17,8327610 secs] 4126207K->4073974K(4126208K), [CMS Perm : 120062K->120053K(201192K)] icms_dc=100 , 17,8329750 secs]
    GCParseRule iCMS_FULL = new GCParseRule("iCMS_FULL", FULL_GC_PREFIX + DATE_TIMESTAMP + "\\[CMS: " + BEFORE_AFTER_CONFIGURED_PAUSE + "\\] " + BEFORE_AFTER_CONFIGURED + ", " + PERM_RECORD + ICMS_DC + ", " + PAUSE_TIME + "\\]");

    //85.577: [Full GC 85.577: [CMS: 5K->1883K(1926784K), 9.8521080 secs] 20369K->1883K(2080128K), [CMS Perm : 13770K->13604K(22970K)] icms_dc=100 , 9.8522900 secs]
    //25.846: [Full GC 25.847: [CMS: 0K->7019K(2015232K), 5.6129510 secs] 11161K->7019K(2097088K), [CMS Perm : 68104K->67271K(13520K)] icms_dc=0 , 25.6146105 secs]
    GCParseRule FULL_GC_ICMS = new GCParseRule("FULL_GC_ICMS", FULL_GC_PREFIX + TIMESTAMP + "\\[CMS: " + BEFORE_AFTER_CONFIGURED_PAUSE + "\\] " + BEFORE_AFTER_CONFIGURED + ", " + PERM_RECORD + ICMS_DC + ", " + PAUSE_TIME + "\\]");

    //91057.643: [GC 91057.643: [ParNew: 153344K->153344K(153344K), 0.0000250 secs]91057.643: [CMS: 1895319K->1754894K(1926784K), 9.6910090 secs] 2048663K->1754894K(2080128K), [CMS Perm : 135228K->134975K(230652K)] icms_dc=65 , 9.6912470 secs]
    GCParseRule iCMS_MISLABELED_FULL = new GCParseRule("iCMS_MISLABELED_FULL", GC_PREFIX + PARNEW_BLOCK + CMS_TENURED_BLOCK + " " + BEFORE_AFTER_CONFIGURED + ", " + PERM_RECORD + ICMS_DC + ", " + PAUSE_TIME);

    /* promotion failures */

    //(concurrent mode failure): 8465K->22006K(1926784K), 0.3222180 secs] 28136K->22006K(2080128K), [CMS Perm : 51702K->51627K(52016K)] icms_dc=5 , 0.3224000 secs]
    //(concurrent mode failure): 1696350K->613432K(2015232K), 22.0030939 secs] 1772267K->613432K(2097088K) icms_dc=100 , 22.3467890 secs]
    GCParseRule iCMS_CONCURRENT_MODE_FAILURE = new GCParseRule("iCMS_CONCURRENT_MODE_FAILURE", "^\\(concurrent mode failure\\): " + BEFORE_AFTER_CONFIGURED_PAUSE + "\\] " + BEFORE_AFTER_CONFIGURED + "(?:, " + PERM_RECORD + ")?" + ICMS_DC + ", " + PAUSE_TIME + "\\]");
    GCParseRule iCMS_CONCURRENT_MODE_FAILURE_META = new GCParseRule("iCMS_CONCURRENT_MODE_FAILURE_META", "^\\(concurrent mode failure\\): " + BEFORE_AFTER_CONFIGURED_PAUSE + "\\] " + BEFORE_AFTER_CONFIGURED + "(?:, " + META_RECORD + ")?" + ICMS_DC + ", " + PAUSE_TIME + "\\]");

    //(concurrent mode interrupted): 2286052K->845009K(3908584K), 6.1999933 secs] 2673522K->845009K(4671912K), [CMS Perm : 137744K->133435K(228656K)] icms_dc=0 , 6.2007727 secs] [Times: user=7.11 sys=0.02, real=6.20 secs]
    GCParseRule iCMS_CONCURRENT_MODE_INTERRUPTED = new GCParseRule("iCMS_CONCURRENT_MODE_INTERRUPTED", "^\\(concurrent mode interrupted\\): " + BEFORE_AFTER_CONFIGURED_PAUSE + "\\] " + BEFORE_AFTER_CONFIGURED + ", " + PERM_RECORD + ICMS_DC + ", " + PAUSE_TIME);

    /*
     * This record was produced *after* CMS-reset completed. Fragmentation failure????
     * 16253.906: [GC 16253.906: [ParNew (promotion failed)
     * Desired survivor size 32768 bytes, new threshold 0 (max 0)
     * : 81792K->81792K(81856K), 0.2765545 secs]16254.183: [CMS (concurrent mode failure): 1552479K->933994K(2015232K), 22.9932464 secs] 1611199K->933994K(2097088K) icms_dc=57 , 23.2713493 secs]
     */
    GCParseRule iCMS_CMF_DUIRNG_PARNEW_DEFNEW_DETAILS = new GCParseRule("iCMS_CMF_DUIRNG_PARNEW_DEFNEW_DETAILS", ": " + BEFORE_AFTER_CONFIGURED_PAUSE + "\\]" + DATE_TIMESTAMP + "\\[CMS \\(concurrent mode failure\\): " + BEFORE_AFTER_CONFIGURED_PAUSE + "\\] " + BEFORE_AFTER_CONFIGURED + ICMS_DC + ", " + PAUSE_TIME + "\\]");

    //35305.590: [GC 35305.590: [ParNew (promotion failed): 10973K->3147K(153344K), 0.0295730 secs] 1763339K->1763817K(2080128K) icms_dc=32 , 0.0297680 secs]
    GCParseRule iCMS_PROMOTION_FAILED = new GCParseRule("iCMS_PROMOTION_FAILED", GC_PREFIX + TIMESTAMP + "\\[ParNew \\(promotion failed\\): " + BEFORE_AFTER_CONFIGURED_PAUSE + "\\]" + " " + BEFORE_AFTER_CONFIGURED + ICMS_DC + ", " + PAUSE_TIME);

    //72825.712: [GC72825.712: [ParNew (promotion failed): 153344K->153344K(153344K), 0.3895590 secs]72826.102: [CMS: 1831960K->1554399K(1926784K), 8.3796720 secs] 1914705K->1554399K(2080128K), [CMS Perm : 131704K->130876K(222768K)] icms_dc=42 , 8.7694530 secs]
    GCParseRule iCMS_PROMOTION_FAILED_PERM = new GCParseRule("iCMS_PROMOTION_FAILED_PERM", GC_PREFIX + PARNEW_PROMOTION_FAILED_BLOCK + CMS_TENURED_BLOCK + " " + BEFORE_AFTER_CONFIGURED + ", " + PERM_RECORD + ICMS_DC + ", " + PAUSE_TIME);
    //very unlikely to see this
    GCParseRule iCMS_PROMOTION_FAILED_META = new GCParseRule("iCMS_PROMOTION_FAILED_META", GC_PREFIX + PARNEW_PROMOTION_FAILED_BLOCK + CMS_TENURED_BLOCK + " " + BEFORE_AFTER_CONFIGURED + ", " + META_RECORD + ICMS_DC + ", " + PAUSE_TIME);

    //10110.232: [Full GC 10110.232: [CMS (concurrent mode failure): 2715839K->1659203K(2752512K), 39.3188254 secs] 2740830K->1659203K(3106432K), [CMS Perm : 206079K->204904K(402264K)] icms_dc=100 , 39.3199631 secs] [Times: user=39.37 sys=0.02, real=39.32 secs]
    GCParseRule iCMS_FULL_AFTER_CONCURRENT_MODE_FAILURE = new GCParseRule("iCMS_FULL_AFTER_CONCURRENT_MODE_FAILURE", FULL_GC_PREFIX + DATE_TIMESTAMP + "\\[CMS \\(concurrent mode failure\\): " + BEFORE_AFTER_CONFIGURED_PAUSE + "\\] " + BEFORE_AFTER_CONFIGURED + ", " + PERM_RECORD + ICMS_DC + ", " + PAUSE_TIME);
    //Very highly unlikely that we'll ever see this record but since it's possible...
    GCParseRule iCMS_FULL_AFTER_CONCURRENT_MODE_FAILURE_META = new GCParseRule("iCMS_FULL_AFTER_CONCURRENT_MODE_FAILURE_META", FULL_GC_PREFIX + DATE_TIMESTAMP + "\\[CMS \\(concurrent mode failure\\): " + BEFORE_AFTER_CONFIGURED_PAUSE + "\\] " + BEFORE_AFTER_CONFIGURED + ", " + META_RECORD + ICMS_DC + ", " + PAUSE_TIME);

    //2016-08-17T10:41:41.088-0500: 76388.020: [GC (Allocation Failure) 2016-08-17T10:41:41.088-0500: 76388.020: [ParNew (promotion failed): 428321K->431492K(471872K), 0.1049894 secs]2016-08-17T10:41:41.193-0500: 76388.125: [CMS: 466697K->186596K(524288K), 0.7357085 secs] 893715K->186596K(996160K), [Metaspace: 46524K->46524K(1097728K)] icms_dc=0 , 0.8408171 secs] [Times: user=0.91 sys=0.00, real=0.84 secs]


}
