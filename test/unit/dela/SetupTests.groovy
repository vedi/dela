package dela

import grails.test.GrailsUnitTestCase

class SetupTests extends GrailsUnitTestCase {
    protected void setUp() {
        super.setUp()
    }

    protected void tearDown() {
        super.tearDown()
    }

    void testSomething() {
        def anotherSetup = new Setup()
        mockDomain(Setup.class, [anotherSetup])
        anotherSetup = Setup.get(1)
        anotherSetup.merge()
    }
}
