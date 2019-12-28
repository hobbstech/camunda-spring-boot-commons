package io.github.hobbstech.bpm.task;

import lombok.val;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;

@Component
public class TaskRepository {

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public TaskRepository(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }

    public Collection<TaskModel> findTasksForProcessAndTaskDefinitionKey(String processInstanceId,
                                                                         String taskDefinitionKey) {

        val query = "SELECT * from ACT_HI_TASKINST WHERE PROC_INST_ID_ = :processInstanceId and TASK_DEF_KEY_ = " +
                ":taskDefinitionKey";

        val namedParameters = new MapSqlParameterSource()
                .addValue("processInstanceId", processInstanceId)
                .addValue("taskDefinitionKey", taskDefinitionKey);

        return namedParameterJdbcTemplate.query(query, namedParameters, new TaskRowMapper());

    }

    public Collection<TaskModel> findTasksForProcess(String processInstanceId) {

        val query = "SELECT * from ACT_HI_TASKINST WHERE PROC_INST_ID_ = :processInstanceId order by START_TIME_ desc";

        val namedParameters = new MapSqlParameterSource()
                .addValue("processInstanceId", processInstanceId);

        return namedParameterJdbcTemplate.query(query, namedParameters, new TaskRowMapper());

    }

    static class TaskRowMapper implements RowMapper<TaskModel> {

        @Override
        public TaskModel mapRow(ResultSet rs, int rowNum) throws SQLException {

            val dto = new TaskModel();

            dto.setId(rs.getString("ID_"));
            dto.setName(rs.getString("NAME_"));
            dto.setAssignee(rs.getString("ASSIGNEE_"));
            dto.setCreated(rs.getDate("START_TIME_"));
            dto.setDue(rs.getDate("DUE_DATE_"));
            dto.setFollowUp(rs.getDate("FOLLOW_UP_DATE_"));

            dto.setDescription(rs.getString("DESCRIPTION_"));
            dto.setExecutionId(rs.getString("EXECUTION_ID_"));
            dto.setOwner(rs.getString("OWNER_"));
            dto.setParentTaskId(rs.getString("PARENT_TASK_ID_"));
            dto.setPriority(rs.getInt("PRIORITY_"));
            dto.setProcessDefinitionId(rs.getString("PROC_DEF_ID_"));
            dto.setProcessInstanceId(rs.getString("PROC_INST_ID_"));
            dto.setTaskDefinitionKey(rs.getString("TASK_DEF_KEY_"));
            dto.setCaseDefinitionId(rs.getString("CASE_DEF_ID_"));
            dto.setCaseExecutionId(rs.getString("CASE_EXECUTION_ID_"));
            dto.setCaseInstanceId(rs.getString("CASE_INST_ID_"));
            dto.setTenantId(rs.getString("TENANT_ID_"));

            return dto;
        }
    }

}
