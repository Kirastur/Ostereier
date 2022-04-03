package de.quadrathelden.ostereier.events;

import de.quadrathelden.ostereier.config.design.ConfigDesign;
import de.quadrathelden.ostereier.config.spawnpoints.ConfigSpawnpointCollection;

public record ReloadDesignResult(ConfigDesign configDesign, ConfigSpawnpointCollection configSpawnpointCollection) {

}
