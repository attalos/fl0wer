package org.attalos.fl0wer;

;

import org.attalos.fl0wer.controll.FL_0_werTest;
import org.attalos.fl0wer.rete.ReteNetworkTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;


@RunWith(Suite.class)
@Suite.SuiteClasses({
        ReteNetworkTest.class,
        FL_0_werTest.class
})

public class FeatureTestSuite {
    // the class remains empty,
    // used only as a holder for the above annotations
}
