package com.junwang.volleyball.model;

import com.junwang.volleyball.model.ModelRepoImpl;
import com.junwang.volleyball.model.ModelRepository;

/**
 * Created by junwang on 28/01/2017.
 */

public class ModelRepoFactory {
    private static ModelRepository repository = new ModelRepoImpl();
    static public ModelRepository getModelRepo() {
        return repository;
    }
}
