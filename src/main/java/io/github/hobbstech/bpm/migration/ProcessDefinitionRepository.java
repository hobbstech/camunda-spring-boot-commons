package io.github.hobbstech.bpm.migration;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

import static java.util.Comparator.comparing;
import static java.util.Objects.nonNull;

@Component
@Slf4j
public class ProcessDefinitionRepository {

    private final JdbcTemplate jdbcTemplate;

    public ProcessDefinitionRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Map<ProcessDefinition, Collection<ProcessDefinition>> getProcessDefinitions(String processId) {

        val result = new HashMap<ProcessDefinition, Collection<ProcessDefinition>>();

        val parameters = new Object[]{processId};

        val allProcessDefinitionsForKey = jdbcTemplate.query("SELECT KEY_, ID_, VERSION_ FROM ACT_RE_PROCDEF " +
                "WHERE KEY_ = ?", parameters, new ProcessDefinitionRowMapper());

        if (nonNull(allProcessDefinitionsForKey)) {

            val latestProcess = allProcessDefinitionsForKey.stream().max(comparing(ProcessDefinition::getVersion));

            if (latestProcess.isPresent()) {

                allProcessDefinitionsForKey.remove(latestProcess.get());
                result.put(latestProcess.get(), allProcessDefinitionsForKey);

            }

        }


        log.info("---> Process Definitions : {}", result.size());

        return result;

    }


    @Data
    static class ProcessDefinition {

        private String id;

        private String key;

        private Integer version;

    }

    public static class ProcessDefinitionRowMapper implements ResultSetExtractor<List<ProcessDefinition>> {

        @Override
        public List<ProcessDefinition> extractData(ResultSet rs) throws SQLException, DataAccessException {

            val processDefinitions = new ArrayList<ProcessDefinition>();

            while (rs.next()) {
                val processDefinition = new ProcessDefinition();

                processDefinition.setId(rs.getString("ID_"));
                processDefinition.setKey(rs.getString("KEY_"));
                processDefinition.setVersion(rs.getInt("VERSION_"));
                processDefinitions.add(processDefinition);
            }

            return processDefinitions;

        }

    }

}
