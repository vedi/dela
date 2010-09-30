package dela

import grails.test.GrailsUnitTestCase

class StoreServiceTests extends GrailsUnitTestCase {

    def anonymous
    def commonDataService
    
    protected void setUp() {

        super.setUp()

        anonymous = new Account()
        commonDataService = [getAnonymous: {anonymous}] as DataService

        mockDomain(State.class, [new State(name:"s1"), new State(name:"s2"), new State(name:"s3")])
        mockDomain(Subject.class)
    }

    protected void tearDown() {
        super.tearDown()
    }

    void testGetSessionContext() {
        def storeService = new StoreService(accountService: commonDataService)
        storeService.afterPropertiesSet()

        def sessionContext = storeService.getSessionContext()

        assertNotNull(sessionContext)
        assertSame(anonymous, sessionContext.account)
        assertNotNull(sessionContext.setup)
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

        // TODO: Change to accountService.anonymous
        def dataControl = mockFor(CommonDataService.class)
        dataControl.demand.getAnonymous(1..1000) {-> return anonymousAccount}

        def storeService = new StoreService(accountService:dataControl.createMock())
        storeService.afterPropertiesSet()

        assert !storeService.isLoggedIn()

        def setup
        
        //TODO: storeService.setup replace with dataContext.setup

        // Anonymous setup
        setup = storeService.setup
        assertSame(anonymousAccount.setup, setup)

        // Setupless account setup
        auth(anotherAccount, storeService)
        setup = storeService.setup
        assertNotSame(anonymousAccount.setup, setup)

        // And more one
        assertSame(setup, storeService.setup)

        // And save it now
        setup = storeService.setup
        anotherAccount.setup = setup
        anotherAccount.save()
        assertSame(setup, storeService.setup)
    }

    void testSetSetup() {
        def storeService = new StoreService(accountService:commonDataService)
        storeService.afterPropertiesSet()

        assert !storeService.isLoggedIn()

        def anotherSetup = new Setup()

        // For anonymous
        storeService.setup = anotherSetup
        assertNotNull(!storeService.sessionContext.setup)

        storeService.sessionContext.setup = null
        assert !storeService.sessionContext.setup

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

    void testConfirmRegistration() {
        def messageService = new MessageService() // TODO: Inject messageService dependencies

        def storeService = new StoreService(messageService:messageService, accountService: this.commonDataService)
        storeService.afterPropertiesSet()

        def final RIGHT_UUID = 'rightUuid'
        def final ANOTHER_RIGHT_UUID = 'anotherRightUuid'
        def final WRONG_UUID = 'wrongUuid'
        def final MISSING_UUID = 'missingUuid'

        def final RIGHT_PASSWORD = "newPassword"

        def rightAccount = new Account(
                login: 'login',
                email: 'login@mail.ml',
                password: RIGHT_UUID,
                role: Account.ROLE_USER,
                state: Account.STATE_CREATING,
                setup: null,
        )

        def wrongAccount = new Account(
                login: 'anotherLogin',
                email: 'anotherLogin@mail.ml',
                password: ANOTHER_RIGHT_UUID,
                role: Account.ROLE_USER,
                state: Account.STATE_CREATING,
                setup: null,
        )

        mockDomain(Account.class, [rightAccount, wrongAccount])
        mockDomain(Subject.class)

        assert Subject.getAll().isEmpty()

        // Check null params
        shouldFail {storeService.confirmRegistration(null, RIGHT_PASSWORD)}
        shouldFail {storeService.confirmRegistration(RIGHT_UUID, null)}


        // Check not existent uuid
        assertFalse(storeService.confirmRegistration(MISSING_UUID, RIGHT_PASSWORD))

        // Check wrong state
        assertFalse(storeService.confirmRegistration(WRONG_UUID, RIGHT_PASSWORD))

        // Check all right
        assertTrue(storeService.confirmRegistration(RIGHT_UUID, RIGHT_PASSWORD))
        // and defaut subject created
        assertEquals(1, rightAccount.subjects.size())
    }

    private def auth(Account anotherAccount, StoreService storeService) {
        storeService.sessionContext.account = anotherAccount
        storeService.sessionContext.setup = storeService.fillSetup()
    }
}
