package uz.coder.muslimcalendar.di

import kotlinx.serialization.json.Json
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.workmanager.dsl.worker
import org.koin.core.module.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module
import uz.coder.muslimcalendar.SharedPref
import uz.coder.muslimcalendar.data.db.AppDatabase
import uz.coder.muslimcalendar.data.map.CalendarMap
import uz.coder.muslimcalendar.data.network.KtorApiService
import uz.coder.muslimcalendar.data.network.KtorClient
import uz.coder.muslimcalendar.data.repository.CalendarRepositoryImpl
import uz.coder.muslimcalendar.data.repository.NotificationSchedulerImpl
import uz.coder.muslimcalendar.data.repository.SettingsRepositoryImpl
import uz.coder.muslimcalendar.data.service.DownloadWorker
import uz.coder.muslimcalendar.data.service.PrayerAlarmWorker
import uz.coder.muslimcalendar.data.service.QazoReminderWorker
import uz.coder.muslimcalendar.data.service.QuranPlayerManager
import uz.coder.muslimcalendar.domain.location.LocationSupervisor
import uz.coder.muslimcalendar.domain.repository.CalendarRepository
import uz.coder.muslimcalendar.domain.repository.NotificationScheduler
import uz.coder.muslimcalendar.domain.repository.SettingsRepository
import uz.coder.muslimcalendar.domain.usecase.DownloadSurahUseCase
import uz.coder.muslimcalendar.domain.usecase.GetAudioPathUseCase
import uz.coder.muslimcalendar.domain.usecase.GetSuraUseCase
import uz.coder.muslimcalendar.domain.usecase.GetSurahByIdUseCase
import uz.coder.muslimcalendar.domain.usecase.GetSurahByNumberUseCase
import uz.coder.muslimcalendar.domain.usecase.GetSurahUseCase
import uz.coder.muslimcalendar.domain.usecase.LoadQuranArabUseCase
import uz.coder.muslimcalendar.domain.usecase.LoadingUseCase
import uz.coder.muslimcalendar.domain.usecase.OneMonthDayUseCase
import uz.coder.muslimcalendar.domain.usecase.PresentDayUseCase
import uz.coder.muslimcalendar.domain.usecase.RegionUseCase
import uz.coder.muslimcalendar.domain.usecase.RemoveUseCase
import uz.coder.muslimcalendar.presentation.ui.theme.ThemeManager
import uz.coder.muslimcalendar.presentation.viewModel.AdvancedSettingsViewModel
import uz.coder.muslimcalendar.presentation.viewModel.CalendarViewModel
import uz.coder.muslimcalendar.presentation.viewModel.HomeViewModel
import uz.coder.muslimcalendar.presentation.viewModel.LocationSettingsViewModel
import uz.coder.muslimcalendar.presentation.viewModel.NotificationViewModel
import uz.coder.muslimcalendar.presentation.viewModel.PrayerStatisticsViewModel
import uz.coder.muslimcalendar.presentation.viewModel.QuranViewModel
import uz.coder.muslimcalendar.presentation.viewModel.SettingsViewModel
import uz.coder.muslimcalendar.presentation.viewModel.SurahViewModel
import uz.coder.muslimcalendar.presentation.viewModel.TasbehViewModel

val appModule = module {
    // Singletons
    single { AppDatabase.instance(androidContext()) }
    single { SharedPref(androidContext()) }
    single { CalendarMap() }
    single { ThemeManager(androidContext()) }
    single { QuranPlayerManager(androidContext()) }
    single { LocationSupervisor(get()) }
    
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
    
    single(named("downloadClient")) { KtorClient.downloadClient }

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
    viewModel { HomeViewModel(get(), get(), get(), get(), get()) }
    viewModel { LocationSettingsViewModel(get(), get(), get(), get()) }
    viewModel { PrayerStatisticsViewModel(get()) }
    viewModel { QuranViewModel(get(), get(), get()) }
    viewModel { SettingsViewModel(get()) }
    viewModel { SurahViewModel(get(), get(), get(), get(), get(), get(), get()) }
    viewModel { TasbehViewModel(get()) }

    // Workers
    worker { params ->
        PrayerAlarmWorker(get(), params.get(), get(), get(), get(), get())
    }
    worker { params ->
        QazoReminderWorker(get(), params.get(), get())
    }
    worker { params ->
        DownloadWorker(
            context = get(),
            workerParams = params.get(),
            db = get(),
            httpClient = get(named("downloadClient"))
        )
    }
}
