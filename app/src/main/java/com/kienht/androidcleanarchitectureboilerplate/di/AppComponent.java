/*
 * Copyright 2018 Vandolf Estrellado
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.kienht.androidcleanarchitectureboilerplate.di;

import com.kienht.androidcleanarchitectureboilerplate.OICApplication;

import javax.inject.Singleton;

import dagger.Component;
import dagger.android.AndroidInjector;
import dagger.android.support.AndroidSupportInjectionModule;

/**
 * Note:
 * Created by kienht on 4/30/18.
 */

@Singleton
@Component(modules = {AndroidSupportInjectionModule.class,
        AppModule.class,
        SchedulerModule.class,
        DataModule.class,
        NetworkModule.class,
        RepositoryModule.class})
interface AppComponent extends AndroidInjector<OICApplication> {

    @Component.Builder
    abstract class Builder extends AndroidInjector.Builder<OICApplication> {
    }
}
