package dela

import grails.test.GrailsUnitTestCase

class StoreServiceTests extends GrailsUnitTestCase {
    
    protected void setUp() {
        super.setUp()

    }

    protected void tearDown() {
        super.tearDown()
    }

    void testGetSetup() {

        def anonymousSetup
        def anonymousAccount
        def anotherAccount
        def anotherSetup

        mockDomain(State.class)
        mockDomain(Subject.class)

        anonymousSetup = new Setup()

        anonymousAccount = new Account(login:'anonymousAccount', password: 'dummy', email:'none@mail.ml', role:Account.ROLE_ANONYMOUS, setup:anonymousSetup)
        anotherAccount = new Account(login:'anotherAccount', password: 'NOTdummy', email:'another@mail.ml', role:Account.ROLE_USER)

        mockDomain(Account.class, [anonymousAccount, anotherAccount])

        def dataControl = mockFor(DataService)
        dataControl.demand.getAnonymous(1..1000) {-> return anonymousAccount}

        def storeService = new StoreService()
        assert !storeService.origAccount

        storeService.dataService = dataControl.createMock()

        def setup

        // Anonymous setup
        setup = storeService.setup
        assertSame(anonymousAccount.setup, setup)

        // Setupless account setup
        auth(anotherAccount, storeService)
        setup = storeService.setup
        assertNotSame(anonymousAccount.setup, setup)

        // And more one
        assertNotSame(setup, storeService.setup)

        // And save it now
        setup = storeService.setup
        anotherAccount.setup = setup
        anotherAccount.save()
        assertSame(setup, storeService.setup)
    }

    void testSetSetup() {
        def storeService = new StoreService()
        assert !storeService.origAccount
        assert !storeService.@setup

        def anotherSetup = new Setup()

        // For anonymous
        storeService.setup = anotherSetup
        assertNotNull(!storeService.@setup)

        storeService.@setup = null
        assert !storeService.@setup

        // First save
        def anotherAccount = new Account(login:'anotherAccount', password: 'NOTdummy', email:'another@mail.ml', role:Account.ROLE_USER)
        auth(anotherAccount, storeService)
        assert !anotherSetup.account

        mockDomain(Setup.class, [anotherSetup])
        mockDomain(State.class)
        mockDomain(Subject.class)
        mockDomain(Account.class, [anotherAccount])

        storeService.setup = anotherSetup
        assertEquals(anotherAccount, anotherSetup.account)
        assertEquals(anotherSetup, anotherAccount.setup)
    }

    private def auth(Account anotherAccount, StoreService storeService) {
        storeService.origAccount = anotherAccount
    }
}
