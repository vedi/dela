package dela.utils

import dela.SubjectCommand

/**
 * @author vedi
 * date 06.12.10
 * time 6:44
 */
class CommandFactory extends DomainFactory {

    def createSubjectCommand(params = [:]) {
        SubjectCommand subjectCommand = new SubjectCommand(
                id: (new Random()).nextLong(),
                owner: createAccount(),
                name: "subject ${UUID.randomUUID().toString()}",
                description: 'subject description',
                isPublic: true,
        )
        applyParams(subjectCommand, params)
        subjectCommand
    }

}
