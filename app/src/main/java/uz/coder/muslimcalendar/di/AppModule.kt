package uz.coder.muslimcalendar.di

import android.content.Context
import androidx.work.WorkerParameters
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModel
import org.koin.androidx.workmanager.dsl.worker
import org.koin.dsl.module
import uz.coder.muslimcalendar.SharedPref
import uz.coder.muslimcalendar.data.db.AppDatabase
import uz.coder.muslimcalendar.data.map.CalendarMap
import uz.coder.muslimcalendar.data.network.KtorApiService
import uz.coder.muslimcalendar.data.network.KtorClient
import uz.coder.muslimcalendar.data.repository.CalendarRepositoryImpl
import uz.coder.muslimcalendar.data.repository.NotificationSchedulerImpl
import uz.coder.muslimcalendar.data.repository.SettingsRepositoryImpl
import uz.coder.muslimcalendar.domain.repository.CalendarRepository
import uz.coder.muslimcalendar.domain.repository.NotificationScheduler
import uz.coder.muslimcalendar.domain.repository.SettingsRepository
import uz.coder.muslimcalendar.domain.usecase.*
import uz.coder.muslimcalendar.presentation.ui.theme.ThemeManager
import uz.coder.muslimcalendar.presentation.viewModel.*
import uz.coder.muslimcalendar.presentation.viewmodel.SafaHomeViewModel
import uz.coder.muslimcalendar.data.service.*
import kotlinx.serialization.json.Json

val appModule = module {
    // Singletons
    single { AppDatabase.instance(androidContext()) }
    single { SharedPref(androidContext()) }
    single { CalendarMap() }
    single { ThemeManager(androidContext()) }
    
    single { Json {
        ignoreUnknownKeys = true
        isLenient = true
        encodeDefaults = true
        prettyPrint = true
    } }

    // Networking
    single { KtorApiService(
        KtorClient.prayerTimeClient,
        KtorClient.quranArabClient,
        KtorClient.quranUzbekClient
    ) }

    // Repositories
    single<CalendarRepository> { CalendarRepositoryImpl(get(), get(), get(), androidContext(), get()) }
    single<NotificationScheduler> { NotificationSchedulerImpl(androidContext(), get(), get(), get()) }
    single<SettingsRepository> { SettingsRepositoryImpl(androidContext(), get(), get(), get()) }

    // UseCases
    factory { RegionUseCase(get()) }
    factory { RemoveUseCase(get()) }
    factory { GetSuraUseCase(get()) }
    factory { LoadingUseCase(get()) }
    factory { GetSurahUseCase(get()) }
    factory { PresentDayUseCase(get()) }
    factory { OneMonthDayUseCase(get()) }
    factory { GetAudioPathUseCase(get()) }
    factory { GetSurahByIdUseCase(get()) }
    factory { DownloadSurahUseCase(get()) }
    factory { LoadQuranArabUseCase(get()) }
    factory { GetSurahByNumberUseCase(get()) }

    // ViewModels
    viewModel { AdvancedSettingsViewModel(get()) }
    viewModel { CalendarViewModel(androidContext(), get()) }
    viewModel { NotificationViewModel(androidContext(), get(), get()) }
    viewModel { SafaHomeViewModel(get(), get()) }
    viewModel { PrayerStatisticsViewModel(get()) }
    viewModel { QazoViewModel(get()) }
    viewModel { QuranViewModel(get(), get(), get()) }
    viewModel { SettingsViewModel(get()) }
    viewModel { SurahViewModel(get(), get(), get(), get(), get(), get()) }
    viewModel { TasbehViewModel(get()) }

    // Workers
    worker { params ->
        PrayerAlarmWorker(get(), params.get(), get(), get())
    }
    worker { params ->
        QazoReminderWorker(get(), params.get())
    }
}
