package dela

import dela.utils.DomainFactory



class SetupTests extends AbstractDelaUnitTestCase {

    def domainFactory = new DomainFactory()

    protected void setUp() {
        super.setUp()
    }

    protected void tearDown() {
        super.tearDown()
    }

    void testCreateSetup() {
        mockDomain(Account)
        mockDomain(Subject)
        mockDomain(Setup)
        def setup = domainFactory.createSetup()
        boolean result = setup.validate()
        assertTrue(setup.errors.toString(), result)
    }

    void testAccountConstraints() {
        mockDomain(Account)
        mockDomain(Subject)
        mockDomain(Setup)
        def setup = domainFactory.createSetup()
        assert setup.save()

        def anotherSetup = domainFactory.createSetup(account: setup.account)
        assertFalse(anotherSetup.validate())
        assertEquals('unique', anotherSetup.errors['account'])

        anotherSetup.account = null
        assertFalse(anotherSetup.validate())
        assertEquals('nullable', anotherSetup.errors['account'])
    }

    void testActiveSubjectConstraints() {
        mockDomain(Account)
        mockDomain(Subject)
        mockDomain(Setup)
        def setup = domainFactory.createSetup(activeSubject: null)
        assertTrue(setup.validate())
    }
}
