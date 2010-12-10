package dela

/**
 * @author vedi
 * date 03.12.10
 * time 18:09
 */
class SubjectCommand {

    Long id
    Account owner

    String name
    String description
    boolean isPublic

    static constraints = {
        owner(nullable:false)
        name(nullable:false)
        description(nullable:true)
    }

    SubjectCommand() {

    }

    SubjectCommand(Subject subject) {

        this()

        assert subject
        Subject.withTransaction {
            if (subject.id) {
                subject = subject.merge()
                assert subject
            }

            this.id             =   subject.id
            this.owner          =   subject.owner
            this.name           =   subject.name
            this.description    =   subject.description
            this.isPublic       =   subject.isPublic
        }
    }

    def getSubject() {

        Subject.withTransaction {
            def subject = this.id != null ? Subject.get(this.id) : new Subject()

            assert subject

            subject.owner       =   this.owner
            subject.name        =   this.name
            subject.description =   this.description
            subject.isPublic    =   this.isPublic

            subject
        }
    }
}
