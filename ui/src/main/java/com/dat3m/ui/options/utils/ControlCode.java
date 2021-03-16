package com.dat3m.ui.options.utils;

public enum ControlCode {

    TASK, SOURCE, TARGET, MODE, ALIAS, BOUND, TIMEOUT, TEST, CLEAR, GRAPH, RELS, METHOD;

    @Override
    public String toString(){
        switch(this){
            case TASK:
                return "Task";
            case SOURCE:
                return "Source";
            case TARGET:
                return "Target";
            case MODE:
                return "Mode";
            case ALIAS:
                return "Alias";
            case BOUND:
                return "Bound";
            case TIMEOUT:
                return "Timeout";
            case TEST:
                return "Test";
            case CLEAR:
                return "Clear";
            case GRAPH:
                return "Graph";
            case RELS:
                return "Relations";
            case METHOD:
                return "Method";
        }
        return super.toString();
    }

    public String actionCommand(){
        switch(this){
            case TASK:
                return "control_command_task";
            case SOURCE:
                return "control_command_source";
            case TARGET:
                return "control_command_target";
            case MODE:
                return "control_command_mode";
            case ALIAS:
                return "control_command_alias";
            case BOUND:
                return "control_command_bound";
            case TIMEOUT:
                return "control_command_timeout";
            case TEST:
                return "control_command_test";
            case CLEAR:
                return "control_command_clear";
            case GRAPH:
                return "control_command_graph";
            case RELS:
                return "control_command_rels";
            case METHOD:
                return "control_command_method";
        }
        throw new RuntimeException("Illegal EditorCode");
    }
}
