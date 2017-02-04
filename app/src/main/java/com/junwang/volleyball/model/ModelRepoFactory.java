package com.junwang.volleyball.model;

import android.content.Context;

import com.junwang.volleyball.model.ModelRepoImpl;
import com.junwang.volleyball.model.ModelRepository;

/**
 * Created by junwang on 28/01/2017.
 */

public class ModelRepoFactory {
    private static ModelRepository repository;
    static public ModelRepository getModelRepo() {
        if (repository == null) {
            repository = new ModelRepoImpl();
        }
        return repository;
    }
}
