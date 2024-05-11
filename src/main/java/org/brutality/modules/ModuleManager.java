package org.brutality.modules;

import org.brutality.modules.modules.move.SprintModule;
import org.brutality.modules.modules.render.TestModule;
import lombok.Getter;

import java.util.ArrayList;

@Getter
public class ModuleManager extends ArrayList<Module> {
    private final SprintModule sprintModule = new SprintModule();
    private final TestModule testModule = new TestModule();
}
