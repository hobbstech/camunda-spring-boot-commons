package io.github.hobbstech.bpm.migration;

import lombok.Data;

import java.util.Collection;

@Data
public final class CustomMigrationContext {

    Collection<String> sourceProcessDefinitionIds;

    String targetProcessDefinitionId;

    String key;

}
