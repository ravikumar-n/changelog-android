package com.ravikumar.changelogmonitor.di.application

import android.app.Application
import com.ravikumar.changelogmonitor.ChangelogApplication
import com.ravikumar.changelogmonitor.di.ActivityBindingModule
import com.ravikumar.changelogmonitor.di.ApiModule
import com.ravikumar.changelogmonitor.di.DataModule
import com.ravikumar.changelogmonitor.di.GsonModule
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjector
import dagger.android.support.AndroidSupportInjectionModule
import javax.inject.Singleton

@Singleton
@Component(
  modules = [
    AndroidSupportInjectionModule::class,
    ApplicationModule::class,
    DataModule::class,
    GsonModule::class,
    ApiModule::class,
    ActivityBindingModule::class
  ]
)
interface ApplicationComponent : AndroidInjector<ChangelogApplication> {
  @Component.Builder
  interface Builder {
    @BindsInstance fun application(application: Application): Builder
    fun build(): ApplicationComponent
  }
}

