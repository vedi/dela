package dela

import grails.test.GrailsUnitTestCase

class StoreServiceTests extends GrailsUnitTestCase {

    def anonymous
    def accountService

    protected void setUp() {

        super.setUp()

        anonymous = new Account(id: 0)
        accountService = [getAnonymous: {anonymous}] as AccountService

        mockDomain(State.class, [new State(name:"s1"), new State(name:"s2"), new State(name:"s3")])
        mockDomain(Subject.class)
    }

    protected void tearDown() {
        super.tearDown()
    }

    void testGetSessionContext() {
        def storeService = new StoreService(accountService: accountService)
        storeService.afterPropertiesSet()

        def sessionContext = storeService.getSessionContext()

        assertNotNull(sessionContext)
        assertSame(anonymous, sessionContext.account)
        assertNotNull(sessionContext.setup)
    }

    void testSaveSetup() {
        def lastSavedSetup = null
        def accountService = [
                saveSetup: {account, setup ->
                    lastSavedSetup = setup
                    account.setup = setup
                    account
                },
                getAnonymous: {
                    this.anonymous
                }
        ] as AccountService

        def storeService = new StoreService(accountService:accountService)
        storeService.afterPropertiesSet()

        assert !storeService.isLoggedIn()

        def anotherSetup = new Setup()

        // For anonymous
        storeService.saveSetup(anotherSetup)
        assertSame(anotherSetup, storeService.sessionContext.setup)
        assertNotSame(anotherSetup, storeService.sessionContext.account.setup)
        assertNull(lastSavedSetup)

        // First save
        def anotherAccount = new Account(login:'anotherAccount', password: 'NOTdummy', email:'another@mail.ml', role:Account.ROLE_USER)
        auth(anotherAccount, storeService)
        assert storeService.isLoggedIn()

        storeService.saveSetup(anotherSetup)
        assertEquals(anotherSetup, anotherAccount.setup)
        assertSame(anotherSetup, storeService.sessionContext.account.setup)
    }

    void testAuth() {
        def final GOOD_LOGIN = 'goodLogin'
        def final GOOD_PASSWORD = 'goodPassword'
        def final BAD_PASSWORD = 'badPassword'
        def goodAccount = new Account(login: GOOD_LOGIN, password: GOOD_PASSWORD)
        def accountService = [
                auth: {login, password ->
                    (login == GOOD_LOGIN && password == GOOD_PASSWORD)?goodAccount:null
                },
                getAnonymous: {
                    this.anonymous
                }] as AccountService

        def storeService = new StoreService(accountService:accountService)
        storeService.afterPropertiesSet()
        assert anonymous.is(storeService.sessionContext.account)

        storeService.auth(GOOD_LOGIN, BAD_PASSWORD)
        assertSame(anonymous, storeService.sessionContext.account)

        storeService.auth(GOOD_LOGIN, GOOD_PASSWORD)
        assertSame(goodAccount, storeService.sessionContext.account)
    }

    void testLogout() {
        def storeService = new StoreService(accountService:accountService)
        storeService.afterPropertiesSet()

        shouldFail {
            storeService.logout()
        }

        auth(new Account(), storeService)
        assert storeService.isLoggedIn()

        storeService.logout()
        assertFalse(storeService.isLoggedIn())

    }

    void testIsLoggedIn() {
        def storeService = new StoreService(accountService:accountService)
        storeService.afterPropertiesSet()
        assertFalse(storeService.isLoggedIn())
        auth(new Account(), storeService)
        assertTrue(storeService.isLoggedIn())
    }                           

    // TODO: Check VVVVVVVV

    void testConfirmRegistration() {
        def messageService = new MessageService() // TODO: Inject messageService dependencies

        def storeService = new StoreService(messageService:messageService, accountService: this.accountService)
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
