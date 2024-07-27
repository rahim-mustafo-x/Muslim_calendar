package uz.coder.muslimcalendar.todo

import uz.coder.muslimcalendar.R
import uz.coder.muslimcalendar.models.model.AllahName

const val REGION = "region"
const val DEFAULT_REGION = "Urganch"
const val ALLAH_NAME_INDEX = "allahNameIndex"
const val BOMDOD = "bomdod_time"
const val PESHIN = "peshin_time"
const val ASR = "asr_time"
const val SHOM = "shom_time"
const val XUFTON = "xufton_time"
const val VITR = "vitr_time"
const val ICON_BOMDOD = "b_icon"
const val ICON_QUYOSH = "q_icon"
const val ICON_PESHIN = "p_icon"
const val ICON_ASR = "a_icon"
const val ICON_SHOM = "s_icon"
const val ICON_XUFTON = "x_icon"
const val SOUND_BOMDOD = "b_icon"
const val SOUND_QUYOSH = "q_icon"
const val SOUND_PESHIN = "p_icon"
const val SOUND_ASR = "a_icon"
const val SOUND_SHOM = "s_icon"
const val SOUND_XUFTON = "x_icon"
const val ONE_TIME = "one_time_for_screen"
val MONTH:Array<String> = App.application!!.resources.getStringArray(R.array.months)
val regionList = listOf(
    "Angren", "Bekobod",
    "Andijon", "Xonobod", "Qo'rg'ontepa", "Shahrixon", "Paxtaobod",
    "Buxoro", "Olot", "Qorako'l", "Qorovulbozor",
    "Farg'ona", "Marg'ilon", "Rishton",
    "Jizzax", "Do'stlik", "G'allaorol", "Zomin",
    "Urganch", "Xiva", "Xazorasp", "Shovot",
    "Namangan", "Chortoq", "Kosonsoy", "Chust", "Mingbuloq",
    "Navoiy", "Zarafshon", "Qiziltepa", "Uchquduq",
    "Qarshi", "Koson",
    "Samarqand", "Kattaqo'rg'on", "Urgut",
    "Guliston",
    "Termiz", "Denov", "Boysun"
)
val allahNames = listOf(AllahName("Alloh","O‘z sifatida, zotida yagona, ibodat qilish uchun eng munosib, undan boshqa iloh yo‘q Zot.\n" +
        "\n" +
        "“Alloh” ismi Parvardigorimizning jami go‘zal ismlari maʼnosini o‘zida jamlagan.\n" +
        "\n"),
AllahName("Ar-Rahmon","O‘ta Mehribon, karamli, barcha maxluqotlarga, jumladan, kofirga ham, mo‘minga ham rizq beruvchi Zot.\n" +
"\n" +
"“Rahmon” sifati faqat Alloh taoloning O‘ziga xos. Bu sifat Undan boshqasiga ishlatilmaydi."),
AllahName("Ar-Rahim","Mehribon, Rahmli, qiyomat kuni faqat mo‘minlarga rahm qiluvchi, Haqiqiy imon keltirganlar\n" +
"\n" +
"gunohini avf qilib, jannatga kirituvchi.\n" +
"\n" +
"“Ar-Rahim” sifati “Ar-Rahmon”dan xosroq bo‘lib, “Oxiratda faqat mo‘minlarga shafqat qiluvchi”\n" +
"\n" +
"maʼnosini bildiradi.\n" +
"\n" +
"Yuqorida “Rahmon” sifatini Alloh taolodan o‘zgaga qo‘llab bo‘lmasligi aytildi. Lekin “Rahim”\n" +
"\n" +
"Allohdan o‘zgalarga, jumladan Payg‘ambarimiz sollallohu alayhi va sallamga nisbatan qo‘llanilishi\n" +
"\n" +
"mumkin (Qarang: “Tavba” surasi, 128-oyat)."),
AllahName("Al-Malik","Barcha mulklarning haqiqiy Egasi, Undan o‘zga ega yo‘q. Alloh xohlagan ishini qila oladi, xohlagan\n" +
"\n" +
"narsasini yaratishi mumkin."),
AllahName("Al-Quddus","Har qanday ayb-nuqsondan pok, mukammal Zot. Mutlaq muqaddaslik faqat Allohga xos."),
AllahName("As-Salom","Nuqsonlardan salomat, pok, bandalarini halokatlardan saqlovchi, ularga omonlik, xotirjamlik\n" +
"\n" +
"beruvchi."),
AllahName("Al-Moʻmin","Dunyoda istalgan bandasiga, oxiratda jahannam o‘tidan faqat mo‘minlarga omonlik beruvch,\n" +
"\n" +
"omonatga vafo qiluvchi, bandalariga jannat haqida bashorat berib, qiyomat kuni buning tasdig‘i o‘laroq\n" +
"\n" +
"ularni jannatga kirituvchi Zot."),
AllahName("Al-Muhaymin","Barcha narsani kuzatib turuvchi, maxluqotlarni himoya qiluvchi, ularning har bir holatini bilib\n" +
"\n" +
"turuvch, har bir narsaga shohid bo‘luvch, maxluqotlar ishini tadbir qiluvch, amin, ishonchli,\n" +
"\n" +
"vaʼdasiga vafo qiluvchi."),
AllahName("Al-Aziz","Kuch-quvvat Egasi, hech qachon mag‘lub bo‘lmaydiga, barcha narsadan g‘olib keluvchi."),
AllahName("Al-Jabbor","Maxluqotlari ustidan mutlaq G‘olib, ularga O‘zi xohlagan amr-qaytariqlarni joriy qiluvchi."),
AllahName("Al-Mutakabbir","Ulug‘, Aziz, kibriyo Egasi."),
AllahName("Al-Xoliq","Avval yo‘q bo‘lgan narsalarni Yaratuvchi, har bir narsani aniq o‘lchov bilan xalq qiluvchi."),
AllahName("Al-Boriʼ","Yo‘qdan bor qiluvchi."),
AllahName("Al-Musavvirʼ","Har bir maxluqotiga o‘ziga yarasha, o‘zga maxluqotlardan ajralib turadigan darajada surat beruvch,\n" +
"\n" +
"bandalariga onalari qornida turgan hollarida O‘zi xohlagan tarzda shakl beruvchi."),
AllahName("Al-G‘affor","Haqiqiy tavba qiluvchi bandalari xato-kamchiliklarini, gunohlarini doim kechiruvchi,\n" +
"\n" +
"nuqsonlarini berkituvchi."),
AllahName("Al-Qahhor","Barcha maxluqotlaridan ustun, ularga O‘zining adolatli hukmini yurgizuvchi."),
AllahName("Al-Vahhob","Bandalariga evazni niyat qilmagan holda juda ko‘p neʼmatlar beruvchi."),
AllahName("Ar-Razzoq","Rizqlarni yaratib, ularni maxluqotlariga yetkazuvchi, maxluqotlarini doim rizqlantirib turuvchi."),
AllahName("Al-Fattoh","Bandalariga rizq va rahmat eshiklarini ochuvchi."),
AllahName("Al-Alim","Barcha narsa va hodisalarning botiniy, zohiriy, eng nozik, eng katta jihatlarigacha Biluvchi, ilmi\n" +
"\n" +
"hamma narsani to‘liq qamrab olgan Zot."),
AllahName("Al-Qobiz","O‘z hikmati bilan baʼzi bandalari rizqini tor qiluvchi, o‘lim chog‘ida bandalar ruhini oluvchi."),
AllahName("Al-Bosit","O‘z karami, rahmati bilan xohlagan bandasi rizqini keng qiluvchi, bandalar hayotlik chog‘ida\n" +
"\n" +
"jasadlari ichidagi ruhlarini qo‘yib yuboruvchi."),
AllahName("Al-Xofiz","Kofir, mushrik, osiy bandalar martabasini pasaytiruvchi."),
AllahName("Ar-Rofiʼ","Mo‘min bandalari martabasini baland qiluvchi, avliyo bandalarini O‘ziga yaqinlashtiruvchi."),
AllahName("Al-Muiz","Xohlagan bandasini imon yo‘liga boshlab, aziz-mukarram qiluvchi."),
AllahName("Al-Muzil","Bandalaridan kimni xohlasa, xor qiluvchi, ulardan azizlikni, mukarramlikni oluvchi."),
AllahName("As-Samiʼ","Har bir narsani eshitib turuvchi. Bo‘layotgan har qanday voqea-hodisa Allohning eshitishidan,\n" +
"\n" +
"ilmidan xoli emas."),
AllahName("Al-Basir","Har bir narsani ko‘rib turuvchi, bo‘layotgan har qanday holat Allohning ko‘rishidan chetda qolmaydi."),
AllahName(" Al-Hakam","Mutlaq Hokim. Hech kim Allohning hukmiga eʼtiroz bildirib, qarshilik ko‘rsata olmaydi."),
AllahName("Al-Adl","Adolatli, mutlaq adolat qiluvchi."),
AllahName("Al-Latif","Lutf ko‘rsatuvchi, karamli, muloyim, mehribo, har bir narsaning o‘ta nozik jihatlarigacha bilib\n" +
"\n" +
"turuvchi."),
AllahName("Al-Xabir","Har bir narsaning zohiriy (tashqi), botiniy (ichki) jihatlaridan xabardor Zot."),
AllahName("Al-Halim","Bandalari isyoniga tezda g‘azab qilmaydigan, ularga imkon beruvchi, osiy bandalariga azob berishga\n" +
"\n" +
"shoshilmaydigan, faqat mavridi kelgandagina jazolovchi."),
AllahName(" Al-Azim","Inson aqli, tafakkuri tasavvur qila olmaydigan darajada ulug‘ Zot."),
AllahName("Al-G‘afur","Bandalar gunohlarini kechirib, ayb-kamchiliklarini berkituvchi."),
AllahName("Ash-Shakur","Itoatkor, solih amallar qiluvchi bandalariga nihoyatda ko‘p mukofot beruvchi, oz amal qiluvchi\n" +
"\n" +
"bandalariga ham niyatlariga, ixloslariga yarasha ajr-savob beruvchi."),
AllahName("Al-Aliy","Juda oliy martabal, Uning zotini, sifatini tasavvur qilishga aqllar ojizlik qiladi."),
AllahName("Al-Kabir","O‘ta ulug‘, maxluqotlaridan mutlaq ustun, azaliy, abadiy Zot."),
AllahName("Al-Hafiz","Maxluqotlarini O‘zi xohlagan muddat halokatdan saqlab turuvchi."),
AllahName("Al-Muqit","“Al-Muqit” sifatining birinchi maʼnosi “Al-Hafiz”niki bilan bir xil. Ikkinchi maʼnosi esa\n" +
"\n" +
"har bir maxluqotiga o‘z nasibasini beruvchidir."),
AllahName("Al-Hasib","Kifoya qiluvchi, maxluqotlarini kifoya qiladigan darajada rizqlantiruvchi, qiyomat kuni\n" +
"\n" +
"bandalarini hisob qiluvchi."),
AllahName("Al-Jalil","Ulug‘lik sifatini o‘zida jamlagan Zo, buyuk, ulug‘, oliy."),
AllahName("Al-Karim","Karami, saxovati cheksiz Zot. Inʼom qilish bilan Allohning xazinasi kamayib qolmaydi."),
AllahName("Ar-Raqib","Har bir narsani kuzatib turuvchi."),
AllahName("Al-Mujib","Bandalar ixlos bilan qilgan duolarini ijobat etuvchi."),
AllahName("Al-Vosiʼ","Rahmati barcha narsadan keng, xohlagan bandasi rizqini kengaytiruvchi."),
AllahName("Al-Hakim","Har bir tadbirini hikmat bilan amalga oshiruvchi."),
AllahName("Al-Vadud","O‘zining solih bandalarini yaxshi ko‘ruvchi, ulardan rozi bo‘luvchi, bandalari tomonidan seviluvchi."),
AllahName("Al-Majid","Shuhrati nihoyatda cheksiz, qadri juda balan, karamining cheki yo‘q, o‘ta saxiy Zot."),
AllahName("Al-Bois","Mazkur sifatning ikki xil maʼnosi bor: 1. Tiriltiruvchi. 2. Yuboruvchi.\n" +
"\n" +
"Yaʼni, bandalariga payg‘ambarlar yuboruvchi, barcha maxluqotlarini qiyomat kuni qayta\n" +
"\n" +
"tiriltiruvchi."),
AllahName("Ash-Shahid","Bo‘layotgan har bir narsa, hodisa ustida hozir-shohid bo‘lib turuvchi. Biron narsa Allohning\n" +
"\n" +
"guvohligidan chetda qolmaydi."),
AllahName("Al-Haq","Mavjudligi haqiqatan tasdiqlangan, Haq, Haqni yuzaga chiqaruvchi Zot."),
AllahName("Al-Vakil","Bandalari ishlarini amalga oshiruvchi, ularga manfaat yetkazishga kafil Zot."),
AllahName("Al-Qaviy","Kuch-quvvat Egasi, biron amalni bajarishdan ojiz qolmaydigan Zot. Alloh mutlaq qudrat\n" +
"\n" +
"Sohibidir."),
AllahName("Al-Matin","O‘ta quvvatli, matonatli Zot. Biron ishni amalga oshirgach, Alloh charchamaydi, zaiflashib olmaydi."),
AllahName("Al-Valiy","Yordam beruvch, valiy bandalarini yaxshi ko‘ruvch, butun olamdagi maxluqotlari ishlarini\n" +
"\n" +
"boshqarib turuvchi Zot."),
AllahName("Al-Hamid","Har qanday holatda, har qanday zamonda hamdu sanoga eng munosib, bandalari tomonidan tinimsiz\n" +
"\n" +
"maqtaladigan Zot."),
AllahName("Al-Muhsiy","Har bir narsani O‘z ilmi bilan hisobga oluvchi, ilmi barcha narsani qamragan, har bir narsaning\n" +
"\n" +
"eng nozik jihatlarini ham, eʼtiborga molik tomonlarini ham inobatga oluvchi."),
AllahName("Al-Mubdiʼ","Har bir narsani o‘xshashi yo‘q darajada avvaldan Yaratuvchi, yo‘qdan bor qiluvchi."),
AllahName("Al-Muid","Maxluqotlarini o‘limga qaytaruvchi (yaʼni, o‘ldiruvchi), so‘ng qiyomat kuni ularni yana hayotga\n" +
"\n" +
"qaytaruvchi."),
AllahName("Al-Muhyi","Qiyomatda o‘liklarni qayta tiriltiruvchi, ularga jon ato etuvchi."),
AllahName("Al-Mumit","O‘limni yaratuvchi, xohlagan bandasi jonini xohlagan vaqtida oluvchi."),
AllahName("Al-Hay","Doim tirik, hech qachon o‘lmaydigan Zot.\n" +
"\n" +
"Boqiylik faqat U Zotga xos. O‘lim, foniylik esa maxluqotlarga xos. Bunday sifatlar Allohga\n" +
"\n" +
"nisbat berilmaydi. Alloh barhayotligi bandalar tirikligidan tubdan farq qiladi."),
AllahName("Al-Qayyum","O‘z-o‘zidan qoim bo‘luvchi, boshqalarni ham qoim qiluvchi, har bir narsa ustida guvoh bo‘luvchi."),
AllahName("Al-Vojid","Xohlagan narsasini xohlagan vaqtida topuvchi, hech qachon faqir bo‘lmaydigan darajada boy Zot."),
AllahName("Al-Mojid","Shon-shuhrat Egasi, qadri baland, karamli, saxiy Zot."),
AllahName("Al-Vohid","Yagona, Yolg‘iz, sherigi yo‘q Zot." +
"Alloh taoloning azalda ham sherigi bo‘lmagan, bundan keyin ham bo‘lmaydi. U Zot o‘z sifatida,\n" +
"\n" +
"zotida, ilohlikda, ibodatga munosiblikda Yakkayu Yagonadir."),
AllahName("As-Somad","Mazkur sifat bir necha maʼnolarni anglatadi. Jumladan, “mutlaq Hokim”, “doim barhayot\n" +
"\n" +
"turuvchi”, “hech kimga hojati tushmaydigan, aksincha maxluqotlari hojatini ravo qiluvchi Zot”."),
AllahName("Al-Qodir","Har bir narsaga qodir Zot. Alloh xohlagan ishini qilishdan ojiz emas. Har qanday ish Unga oson."),
AllahName("Al-Muqtadir","Qudrati cheksiz, nihoyatda kuchli Zot."),
AllahName("Al-Muqaddim","Xohlagan narsasini oldinga surib, ularni o‘z joyiga qo‘yuvchi. Kim yoki nima oldinga surilishga\n" +
"\n" +
"munosib bo‘lsa, oldinga suradi."),
AllahName("Al-Muaxxir","Xohlagan narsasini ortga surib, ularni o‘z joyiga qo‘yuvchi. Kim yoki nima ortga surilishga\n" +
"\n" +
"munosib bo‘lsa, ortga suradi."),
AllahName("Al-Avval","Boshlanishining avvali yo‘q, barcha narsadan avval bo‘lgan Zot.\n" +
"\n" +
"Alloh taolo maxluqotlar yaratilmasidan oldin ham mavjud edi. Dunyodagi barcha narsalar,\n" +
"\n" +
"mavjudotlar “Al-Avval” sifatli Alloh tomonidan yaratilgan."),
AllahName("Al-Oxir","Maxluqotlar o‘lib ketganidan so‘ng ham boqiy qoluvchi, hech qachon o‘lmaydigan, mavjudligining\n" +
"\n" +
"oxiri yo‘q Zot."),
AllahName("Az-Zohir","Barcha narsadan ustun, oliy Zot. Atrof-muhitdagi narsalar, holatlar Allohning zohirligiga\n" +
"\n" +
"dalolat qiladi.\n" +
"\n" +
"Haqiqatan, aql yuritgan kishi Allohning borligini, Yakkayu Yagonaligini biladi. Zero, U Zotning\n" +
"\n" +
"mavjudligi ochiq-oydin ko‘rinib turadi."),
AllahName("Al-Botin","Maxluqotlar nazaridan berkingan, ularga ko‘rinmaydigan Zot. Chunonchi, ko‘z bilan Allohni bu\n" +
"\n" +
"dunyoda ko‘rib bo‘lmaydi."),
AllahName("Al-Voliy","Barcha narsaning Egasi, ularni tasarruf etuvchi Zot."),
AllahName("Al-Mutaoliy","Kofirlar U Zotga nisbatan ayb taqashlaridan, mo‘minlar hamdu sanolaridan oliy Zot.\n" +
"\n" +
"Yaʼni, kofir va mushriklarning Alloh taologa nisbatan noloyiq tuhmatlari U Zot shaʼniga putur\n" +
"\n" +
"yetkazmaydi. Mo‘minlar hamdu sanolari esa U Zot ulug‘ligini ziyoda qilmaydi. Chunonchi, bandalari\n" +
"\n" +
"tuhmatlari, hamdu sanolari Alloh taologa zarar ham, foyda ham keltirmaydi. U Zot bandalari\n" +
"\n" +
"qiladigan amallaridan, aytadigan gaplaridan Oliy va Behojatdir."),
AllahName("Al-Bar","Bandalariga cheksiz yaxshiliklar qiluvchi, lutf-karami, ehsonining cheki yo‘q Zot."),
AllahName("At-Tavvob","Bandalari ixlos bilan qilgan tavbalarini qabul qiluvchi."),
AllahName("Al-Muntaqim","G‘azabini qo‘zg‘agan bandalaridan intiqom (qasos) oluvchi, ularni jazolovchi. Lekin Allohning\n" +
"\n" +
"jazolashi zulm emas, adolatdir."),
AllahName("Al-Afuv","Bandalari gunohlarini kechirib yuboruvchi.\n" +
"\n" +
"Mazkur sifat maʼnosi “Al-G‘afur”nikidan ham kuchliroq. Zero “Al-G‘afur” bandalar gunohlarini\n" +
"\n" +
"berkitishni, “Al-Afuv” gunohlarni kechirishni, o‘chirib yuborishni anglatadi."),
AllahName("Ar-Rauf","O‘ta Mehribon, nihoyatda Shafqatli, Rahmli Zot."),
AllahName("Molikul mulk","Mulk egasi, ishlarni O‘zi xohlagan tarzda amalga oshiradi, Uning hukmiga qarshi boruvchi yo‘q,\n" +
"\n" +
"mutlaq tasarruf qiluvchi."),
AllahName("Zul jaloli val ikrom","Ulug‘lik va karam Egasi."),
AllahName("Al-Muqsit","Adolatli Zot.\n" +
"\n" +
"Alloh O‘z hukmida, jazo berishida, mahrum etishida adolatlidir. Bandalariga zulm qilmaydi,\n" +
"\n" +
"ularni gunohlariga yarasha jazolaydi, yaxshi ishlarini munosib taqdirlaydi."),
AllahName("Al-Jomiʼ","Maxluqotlarni hisob qilish uchun qiyomat kuni mahshargohga Jamlovchi."),
AllahName("Al-G‘aniy","Boy, Behojat Zot. Boshqalarning Allohga hojati tushadi, biroq Alloh hech kimga, hech narsaga\n" +
"\n" +
"muhtoj emas."),
AllahName("Al-Mug‘niy","Behojat-boy qiluvchi.\n" +
"\n" +
"Alloh bandalari orasidan kimni xohlasa, o‘shani boy-behojat qilib qo‘yadi."),
AllahName("Al-Moniʼ","O‘ziga itoat etuvchi mo‘min bandalarini har xil kulfatlardan, qiyinchiliklardan asrovchi, ularni\n" +
"\n" +
"balolardan qutqaruvch, xohlagan bandasidan keng rizqni man qiluvchi."),
AllahName("Az-Zor","Zararli narsalarni ham yaratuvchi.\n" +
"\n" +
"Alloh yaxshini ham, yomonni ham, foydalini ham, zararlini ham yaratadi. Shu orqali xohlagan\n" +
"\n" +
"maxluqotiga O‘z hikmati bilan zarar yetkazadi."),
AllahName("An-Nofiʼ","Xohlagan bandasiga manfaat keltiruvchi."),
AllahName("An-Nur","Ko‘zi ojizlar Allohning nuri bilan ko‘radi. Maʼnaviy so‘qirlar Uning hidoyati ila to‘g‘ri yo‘lni\n" +
"\n" +
"topadi. Alloh O‘zi zohir bo‘luvchi, o‘zgalarni ham zohir qiluvchi Zotdir. U osmonlar va Yerning nuridir."),
AllahName("Al-Hodiy","Xohlagan bandasini to‘g‘ri yo‘lga yo‘llovchi, hidoyatga boshlovchi."),
AllahName("Al-Badiʼ","Mislsiz narsalarni yo‘qdan bor qiluvchi."),
AllahName("Al-Boqiy","Doim boqiy turuvchi, foniylik sifatidan xoli."),
AllahName("Al-Voris","Barcha maxluqotlar o‘lib ketganidan keyin ham mangu qoluvchi."),
AllahName("Ar-Rashid","Xohlagan bandasini to‘g‘ri yo‘lga boshlovchi."),
AllahName("As-Sabur","O‘ta sabrli Zot.\n" +
"\n" +
"Alloh gunohkorlarni jazolashga shoshilmaydi, balki hidoyat yo‘liga yurib, o‘zlarini isloh\n" +
"\n" +
"qilishlariga imkon beradi."))