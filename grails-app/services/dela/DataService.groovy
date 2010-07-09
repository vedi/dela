package dela

class DataService {

    static transactional = true

    def loadSetup() {
        Setup.count() ? Setup.findAll()[0] : new Setup()
    }

    def public changeTaskPower(id, double newPower) {
        Task.withTransaction {
            Task task = Task.get(id)
            task.power = newPower;
            def result = task.save()
            assert result
        }
    }

    boolean tryCompleteTask(long id) {
        Task.withTransaction {
            State state = State.get(2) // FIXME: Hardcoded state id
            assert state

            Task task = Task.get(id)
            assert task
            if (!state.equals(task.state)) {
                task.state = state
                def result = task.merge()
                assert result
                return true
            } else {
                return false
            }
        }
    }

    def normalizeSubject(long id) {
        Task.withTransaction {

            Subject subject = Subject.get(id)
            assert subject

            State state = State.get(1) // TODO: Отвязать от id состояния
            assert state

            def count = Task.countBySubjectAndState(subject, state)
            if (count) {
                double step = 1.0 / (count + 1)
                double currentPower = step
                def tasks = Task.findAllBySubjectAndState(subject, state, [sort:'power', order:'asc'])
                tasks.each {task ->
                    task.power = currentPower
                    currentPower += step
                    def result = task.merge()
                    assert result
                }
            }
        }
    }
}
