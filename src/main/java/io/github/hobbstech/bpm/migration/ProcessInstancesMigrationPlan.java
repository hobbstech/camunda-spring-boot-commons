package io.github.hobbstech.bpm.migration;

import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.camunda.bpm.engine.BadUserRequestException;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.migration.MigrationPlan;
import org.springframework.stereotype.Component;

import java.util.HashSet;

@Component
@Slf4j
public class ProcessInstancesMigrationPlan {

    private final RuntimeService runtimeService;

    private final ProcessDefinitionRepository processDefinitionRepository;

    public ProcessInstancesMigrationPlan(RuntimeService runtimeService, ProcessDefinitionRepository processDefinitionRepository) {
        this.runtimeService = runtimeService;
        this.processDefinitionRepository = processDefinitionRepository;
    }

    public void migrate(String processId) {

        val migrationPlans = new HashSet<MigrationPlan>();

        val processesMap = processDefinitionRepository.getProcessDefinitions(processId);

        processesMap.forEach((targetDefinition, processDefinitions) -> processDefinitions.forEach(sourceDefinition -> {

            val migrationPlan = runtimeService
                    .createMigrationPlan(sourceDefinition.getId(), targetDefinition.getId())
                    .mapEqualActivities()
                    .build();

            migrationPlans.add(migrationPlan);

        }));

        migrationPlans.parallelStream().forEach(migrationPlan -> {
            val processInstanceQuery = runtimeService
                    .createProcessInstanceQuery()
                    .processDefinitionId(migrationPlan.getSourceProcessDefinitionId());

            try {
                runtimeService.newMigration(migrationPlan)
                        .processInstanceQuery(processInstanceQuery)
                        .skipCustomListeners()
                        .skipIoMappings()
                        .executeAsync();
            } catch (BadUserRequestException ex) {
                log.error("---> Transfer failed for id : {} due to : {}",
                        migrationPlan.getSourceProcessDefinitionId(), ex.getMessage());
            }
        });


    }


}
