package uz.coder.muslimcalendar.todo

import uz.coder.muslimcalendar.domain.model.AllahName
import uz.coder.muslimcalendar.domain.model.Duo
import uz.coder.muslimcalendar.domain.model.Namoz

const val KEY_BOMDOD = "bomdod"
const val KEY_QUYOSH = "quyoshChiqishi"
const val KEY_PESHIN = "peshin"
const val KEY_ASR = "asr"
const val KEY_SHOM = "shom"
const val KEY_XUFTON = "xufton"
const val REGION = "region"
const val ALLAH_NAME_INDEX = "allahNameIndex"
const val DUO_INDEX = "duo_index"
const val NAMOZ_INDEX = "namoz_index"
const val BOMDOD = "bomdod_time"
const val PESHIN = "peshin_time"
const val ASR = "asr_time"
const val SHOM = "shom_time"
const val XUFTON = "xufton_time"
const val VITR = "vitr_time"
const val TASBEH = "tasbeh"
const val ALL_TASBEH = "all_tasbeh"
const val NUMBER = "number"


val namozList = listOf(
    Namoz(
        "Bomdod",
        """
        1. «Alloh rizoligi uchun qibla tomonga yuzlanib, bugungi bomdod namozining ikki rakat sunnatini o'qishni niyat qildim», deb ko'ngildan o'tkaziladi.
        2. Iftitoh takbiri: «Allohu akbar», deb namozga kiriladi.
        3. Sano duosi o'qiladi.
        4. Fotiha surasi o'qiladi.
        5. Zam sura (qoladan kelganicha) o'qiladi.
        6. Ruku qilinadi.
        7. Rukudan «Sami'allohu liman hamidah», deb tik turiladi.
        8. Tik turgan holda «Robbana lakal hamd», deyiladi.
        9. Sajda qilinadi.
        10. Sajdadan «Allohu akbar», deb o'tiriladi.
        11. «Allohu akbar», deb ikkinchi marta sajda qilinadi.
        12. Sajdadan «Allohu akbar», deb tik turiladi. Bu birinchi rakat bo'ldi.
        13. Ikkinchi rakat ham xuddi shu tartibda davom ettiriladi.
        14. Ikkinchi sajdadan keyin «Allohu akbar», deb o'tiriladi va o'qiladi:
        a) Attahiyyat,
        b) Salovot,
        c) Robbana duo.
        15. Avval o'ng, keyin chap yelka sari boqib: «Assalomu alaykum va rohmatulloh», deb salom beriladi.
        
        Shu bilan bomdod namozining ikki rakat sunnati tugaydi.
        Bomdod namozining ikki rakat farzi ham xuddi shu tartibda o'qiladi.
        Lekin bomdod namozining farzida faqat Fotiha surasi va zam sura o'qiladi. Namoz oxirida «Attahiyyat», «Salovot» va «Robbana» duosi o'qiladi.
        """.trimIndent()
    ),
    Namoz(
        "Peshin",
        """
        Peshin namozi to'rt rakat sunnat, to'rt rakat farz va ikki rakat sunnatdan iborat.
        
        Avval to'rt rakatli sunnat quyidagicha o'qiladi:
        
        1. Niyat qilinadi.
        2. Takbiri tahrima.
        3. Sano.
        4. Fotiha va zam sura.
        5. Ruku.
        6. Rukudan turish.
        7. Sajda.
        8. Sajdadan turish.
        9. Ikkinchi rakat ham shu tartibda o'qilib bo'lingandan so'ng o'tiriladi.
        10. Attahiyyat o'qilib, uchinchi rakatga turiladi.
        11. Faqatgina Fotiha surasi o'qiladi.
        12. Ruku.
        13. Rukudan turish.
        14. Sajda.
        15. Sajdadan turish.
        16. To'rtinchi rakat ham shu tarzda o'qilib bo'lgach o'tiriladi.
        17. Attahiyyat.
        18. Salovot.
        19. Robbana.
        20. Salom.
        
        To'rt rakatli farz namozining o'qilishi ham yuqoridagidek.
        """.trimIndent()
    ),
    Namoz(
        "Asr",
        """
        Asr namozi to'rt rakat farzdan iborat. Bu namoz quyidagicha o'qiladi:
        
        1. Niyat qilinadi.
        2. Takbiri tahrima.
        3. Sano.
        4. Fotiha va zam sura.
        5. Ruku.
        6. Rukudan turish.
        7. Sajda.
        8. Sajdadan turish.
        9. Ikkinchi rakat ham shu tartibda o'qilib bo'lingandan so'ng o'tiriladi.
        10. Attahiyyat o'qilib, uchinchi rakatga turiladi.
        11. Faqatgina Fotiha surasi o'qiladi.
        12. Ruku.
        13. Rukudan turish.
        14. Sajda.
        15. Sajdadan turish.
        16. To'rtinchi rakat ham shu tarzda o'qilib bo'lgach o'tiriladi.
        17. Attahiyyat.
        18. Salovot.
        19. Robbana.
        20. Salom.
        """.trimIndent()
    ),
    Namoz(
        "Shom",
        """
        Shom namozi uch rakat farz, ikki rakat sunnat va ikki rakat nafldan iborat.
        
        1. Uch rakat farzning o'qilishi: avval niyat qilinadi.
        2. Takbiri tahrima.
        3. Sano.
        4. Fotiha va zam sura.
        5. Ruku.
        6. Rukudan turish.
        7. Sajda.
        8. Sajdadan turish.
        9. Ikkinchi rakat ham shu tarzda o'qilib bo'lgach o'tiriladi.
        10. Attahiyyat o'qilib, uchinchi rakatga turiladi.
        11. Faqatgina Fotiha surasi o'qiladi.
        12. Ruku.
        13. Rukudan turish.
        14. Sajda.
        15. Sajdadan turish.
        16. To'rtinchi rakat ham shu tarzda o'qilib bo'lgach o'tiriladi.
        17. Attahiyyat.
        18. Salovot.
        19. Robbana.
        20. Salom.
        """.trimIndent()
    ),
    Namoz(
        "Xufton",
        """
        Xufton namozi to'rt rakat farz, ikki rakat sunnat va uch rakat vitrdan iborat.
        
        Avval to'rt rakatli farz quyidagicha o'qiladi:
        
        1. Niyat qilinadi.
        2. Takbiri tahrima.
        3. Sano.
        4. Fotiha va zam sura.
        5. Ruku.
        6. Rukudan turish.
        7. Sajda.
        8. Sajdadan turish.
        9. Ikkinchi rakat ham shu tarzda o'qilib bo'lgach o'tiriladi.
        10. Attahiyyat o'qilib, uchinchi rakatga turiladi.
        11. Faqatgina Fotiha surasi o'qiladi.
        12. Ruku.
        13. Rukudan turish.
        14. Sajda.
        15. Sajdadan turish.
        16. To'rtinchi rakat ham shu tarzda o'qilib bo'lgach o'tiriladi.
        17. Attahiyyat.
        18. Salovot.
        19. Robbana.
        20. Salom.
        """.trimIndent()
    )
)

val dualist = listOf(
    Duo("Duo","Robbana atina fid-dunya hasanatav-va fil axiroti hasanatav-va qina azaban-nar."),
Duo("Salovat","Allohumma solli ‘ala Muhammadiv-va ‘ala ali Muhammad. Kama sollayta ‘ala Ibrohima va ‘ala ali Ibrohim. Innaka hamidum-majid. Allohumma barik ‘ala Muhammadiv-va ‘ala ali Muhammad. Kama barokta ‘ala Ibrohima va ‘ala ali Ibrohim. Innaka hamidum-majid."),
Duo("Attahiyat duvosi","Attahiyyatu lillahi vas-solavatu vattoyyibat. Assalamu ‘alayka ayyuhan-nabiyyu va rohmatullohi va barokatuh. Assalamu ‘alayna va ‘ala ibadillahis-solihiyn. Ashhadu alla ilaha illallohu va ashhadu anna Muhammadan ‘abduhu va rosuluh."),
Duo("Salovat (qisqa)","Allohumma solli ‘ala sayydina Muhammad"),
Duo("Saloti munjiya","Allohumma solli ‘ala sayyidina Muhammadin va ‘ala ali sayyidina Muhammad, salatan tunjina biha min-jami’il ahvali val afat. Va taqzi lana biha jami’al hajat va tutohhiruna biha min-jami’is-sayyiat. Va tarfa-’una biha ‘alad-darojat va tuballig‘una biha aqsol-g‘oyoti, min-jami’il xoyroti fil-hayati va ba’dal mamat.\n" +
"\n" +
"Allohim! Janobi Payg‘ambarimiz Muhammad Mustafoga beadad salot va salom bo‘lsin. Bu salotlar, salomlar va duolar bizni har turli balo va musibatlardan xalos etsin, barcha ehtiyojlarimizni ketkizib, bizni gunohlardan poklasin. Bizni yuksak daraja va martabalarga ko‘tarsin. Allohim! Bu salot va salomlar tufayli o‘lgandan ke-yin xayr (yaxshilik)larga noil bo‘laylik. "),
Duo("Saloti fathiyya ","Allohumma solli va sallim va barik ‘ala sayyidina Muhammadinil-fatihi lima ug‘liqo val-xotimi lima sabaqo, Nasiril-haqqi bilhaqqi, val hadi ila sirotikal-mustaqim va ‘ala alihi haqqo qadrihi va miqdarihil-’aziym.\n" +
"\n" +
"Allohim! Suyukli Payg‘ambarimiz Muhammad Mustafoga salot va salomlar bo‘lsin. Bizga Sening yashirin xazinalaringni ochib bergan Udir. Payg‘ambarlar halqasining oxiri Udir, Haqning eng katta yordamchisi Udir, insonlarga to‘g‘ri yo‘lni ko‘rsatgan Udir! Unga va yaqinlariga so‘ngsiz salot va salomlar bo‘lsin. "),
Duo("Boshqa bir salovot ","Allohumma solli ‘ala sayyidina Muhammadinin-nabiyyil-ummiyyi va ‘ala alihi va sahbihi va sallim. \n" +
"\n"),
Duo("Azon Duosi","Allohumma robba hazihid da’vatit tammah. Vas-solatil qoimah, ati Muhammadanil vasiylata val faziylah. Vad-darojatal ’aliyatar rofi’a. Vab’as-hu maqomam mahmudanillaziy va’adtah. Varzuqna shafa-’atahu yavmal qiyamah. Innaka la tuxliful mi’ad!"),
Duo("Qunut duosi","«Allohumma innaa nasta’iynuka va nastag‘firuka. Va nu’minu bika va natavakkalu alayka. Va nusniy alaykal xoyro kullahu. Nashkuruka va laa nakfuruk. Va naxla’u va natruku man yafjuruk. Allohumma iyyaaka na’budu va laka nusolliy va nasjudu va ilayka nas’aa va nahfidu. Narjuu rohmataka va naxshaa azaabaka. Inna azaabaka bil kuffaari mulhiq»."),
Duo("Ro‘za tutish (saharlik, og‘iz yopish) duosi","نَوَيْتُ أَنْ أَصُومَ صَوْمَ شَهْرَ رَمَضَانَ مِنَ الْفَجْرِ إِلَى الْمَغْرِبِ، خَالِصًا لِلهِ تَعَالَى أَللهُ أَكْبَرُ\n" +
"\n" +
"Navaytu an asuvma sovma shahri ramazona minal fajri ilal mag‘ribi, xolisan lillahi taʼaalaa Allohu akbar.\n" +
"\n" +
"Maʼnosi: Ramazon oyining ro‘zasini subhdan to kun botguncha tutmoqni niyat qildim. Xolis Alloh uchun Alloh buyukdir."),
Duo("Iftorlik (og‘iz ochish) duosi","اَللَّهُمَّ لَكَ صُمْتُ وَ بِكَ آمَنْتُ وَ عَلَيْكَ تَوَكَّلْتُ وَ عَلَى رِزْقِكَ أَفْتَرْتُ، فَغْفِرْلِى مَا قَدَّمْتُ وَ مَا أَخَّرْتُ بِرَحْمَتِكَ يَا أَرْحَمَ الرَّاحِمِينَ\n" +
"\n" +
"Allohumma laka sumtu va bika aamantu va aʼlayka tavakkaltu va aʼlaa rizqika aftartu, fag‘firliy ma qoddamtu va maa axxortu birohmatika yaa arhamar roohimiyn.\n" +
"\n" +
"Maʼnosi: Ey Alloh, ushbu Ro‘zamni Sen uchun tutdim va Senga iymon keltirdim va Senga tavakkal qildim va bergan rizqing bilan iftor qildim. Ey mehribonlarning eng mehriboni, mening avvalgi va keyingi gunohlarimni mag‘firat qilgil.")
)
val allahNames = listOf(
    AllahName(
        "Alloh",
        "O‘z sifatida, zotida yagona, ibodat qilish uchun eng munosib, undan boshqa iloh yo‘q Zot.\n" +
                "\n" +
                "“Alloh” ismi Parvardigorimizning jami go‘zal ismlari maʼnosini o‘zida jamlagan.\n" +
                "\n"
    ),
    AllahName(
        "Ar-Rahmon",
        "O‘ta Mehribon, karamli, barcha maxluqotlarga, jumladan, kofirga ham, mo‘minga ham rizq beruvchi Zot.\n" +
                "\n" +
                "“Rahmon” sifati faqat Alloh taoloning O‘ziga xos. Bu sifat Undan boshqasiga ishlatilmaydi."
    ),
    AllahName(
        "Ar-Rahim",
        "Mehribon, Rahmli, qiyomat kuni faqat mo‘minlarga rahm qiluvchi, Haqiqiy imon keltirganlar\n" +
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
                "mumkin (Qarang: “Tavba” surasi, 128-oyat)."
    ),
    AllahName(
        "Al-Malik",
        "Barcha mulklarning haqiqiy Egasi, Undan o‘zga ega yo‘q. Alloh xohlagan ishini qila oladi, xohlagan\n" +
                "\n" +
                "narsasini yaratishi mumkin."
    ),
    AllahName(
        "Al-Quddus",
        "Har qanday ayb-nuqsondan pok, mukammal Zot. Mutlaq muqaddaslik faqat Allohga xos."
    ),
    AllahName(
        "As-Salom",
        "Nuqsonlardan salomat, pok, bandalarini halokatlardan saqlovchi, ularga omonlik, xotirjamlik\n" +
                "\n" +
                "beruvchi."
    ),
    AllahName(
        "Al-Moʻmin",
        "Dunyoda istalgan bandasiga, oxiratda jahannam o‘tidan faqat mo‘minlarga omonlik beruvch,\n" +
                "\n" +
                "omonatga vafo qiluvchi, bandalariga jannat haqida bashorat berib, qiyomat kuni buning tasdig‘i o‘laroq\n" +
                "\n" +
                "ularni jannatga kirituvchi Zot."
    ),
    AllahName(
        "Al-Muhaymin",
        "Barcha narsani kuzatib turuvchi, maxluqotlarni himoya qiluvchi, ularning har bir holatini bilib\n" +
                "\n" +
                "turuvch, har bir narsaga shohid bo‘luvch, maxluqotlar ishini tadbir qiluvch, amin, ishonchli,\n" +
                "\n" +
                "vaʼdasiga vafo qiluvchi."
    ),
    AllahName(
        "Al-Aziz",
        "Kuch-quvvat Egasi, hech qachon mag‘lub bo‘lmaydiga, barcha narsadan g‘olib keluvchi."
    ),
    AllahName(
        "Al-Jabbor",
        "Maxluqotlari ustidan mutlaq G‘olib, ularga O‘zi xohlagan amr-qaytariqlarni joriy qiluvchi."
    ),
    AllahName(
        "Al-Mutakabbir",
        "Ulug‘, Aziz, kibriyo Egasi."
    ),
    AllahName(
        "Al-Xoliq",
        "Avval yo‘q bo‘lgan narsalarni Yaratuvchi, har bir narsani aniq o‘lchov bilan xalq qiluvchi."
    ),
    AllahName(
        "Al-Boriʼ",
        "Yo‘qdan bor qiluvchi."
    ),
    AllahName(
        "Al-Musavvirʼ",
        "Har bir maxluqotiga o‘ziga yarasha, o‘zga maxluqotlardan ajralib turadigan darajada surat beruvch,\n" +
                "\n" +
                "bandalariga onalari qornida turgan hollarida O‘zi xohlagan tarzda shakl beruvchi."
    ),
    AllahName(
        "Al-G‘affor",
        "Haqiqiy tavba qiluvchi bandalari xato-kamchiliklarini, gunohlarini doim kechiruvchi,\n" +
                "\n" +
                "nuqsonlarini berkituvchi."
    ),
    AllahName(
        "Al-Qahhor",
        "Barcha maxluqotlaridan ustun, ularga O‘zining adolatli hukmini yurgizuvchi."
    ),
    AllahName(
        "Al-Vahhob",
        "Bandalariga evazni niyat qilmagan holda juda ko‘p neʼmatlar beruvchi."
    ),
    AllahName(
        "Ar-Razzoq",
        "Rizqlarni yaratib, ularni maxluqotlariga yetkazuvchi, maxluqotlarini doim rizqlantirib turuvchi."
    ),
    AllahName(
        "Al-Fattoh",
        "Bandalariga rizq va rahmat eshiklarini ochuvchi."
    ),
    AllahName(
        "Al-Alim",
        "Barcha narsa va hodisalarning botiniy, zohiriy, eng nozik, eng katta jihatlarigacha Biluvchi, ilmi\n" +
                "\n" +
                "hamma narsani to‘liq qamrab olgan Zot."
    ),
    AllahName(
        "Al-Qobiz",
        "O‘z hikmati bilan baʼzi bandalari rizqini tor qiluvchi, o‘lim chog‘ida bandalar ruhini oluvchi."
    ),
    AllahName(
        "Al-Bosit",
        "O‘z karami, rahmati bilan xohlagan bandasi rizqini keng qiluvchi, bandalar hayotlik chog‘ida\n" +
                "\n" +
                "jasadlari ichidagi ruhlarini qo‘yib yuboruvchi."
    ),
    AllahName(
        "Al-Xofiz",
        "Kofir, mushrik, osiy bandalar martabasini pasaytiruvchi."
    ),
    AllahName(
        "Ar-Rofiʼ",
        "Mo‘min bandalari martabasini baland qiluvchi, avliyo bandalarini O‘ziga yaqinlashtiruvchi."
    ),
    AllahName(
        "Al-Muiz",
        "Xohlagan bandasini imon yo‘liga boshlab, aziz-mukarram qiluvchi."
    ),
    AllahName(
        "Al-Muzil",
        "Bandalaridan kimni xohlasa, xor qiluvchi, ulardan azizlikni, mukarramlikni oluvchi."
    ),
    AllahName(
        "As-Samiʼ",
        "Har bir narsani eshitib turuvchi. Bo‘layotgan har qanday voqea-hodisa Allohning eshitishidan,\n" +
                "\n" +
                "ilmidan xoli emas."
    ),
    AllahName(
        "Al-Basir",
        "Har bir narsani ko‘rib turuvchi, bo‘layotgan har qanday holat Allohning ko‘rishidan chetda qolmaydi."
    ),
    AllahName(
        " Al-Hakam",
        "Mutlaq Hokim. Hech kim Allohning hukmiga eʼtiroz bildirib, qarshilik ko‘rsata olmaydi."
    ),
    AllahName(
        "Al-Adl",
        "Adolatli, mutlaq adolat qiluvchi."
    ),
    AllahName(
        "Al-Latif",
        "Lutf ko‘rsatuvchi, karamli, muloyim, mehribo, har bir narsaning o‘ta nozik jihatlarigacha bilib\n" +
                "\n" +
                "turuvchi."
    ),
    AllahName(
        "Al-Xabir",
        "Har bir narsaning zohiriy (tashqi), botiniy (ichki) jihatlaridan xabardor Zot."
    ),
    AllahName(
        "Al-Halim",
        "Bandalari isyoniga tezda g‘azab qilmaydigan, ularga imkon beruvchi, osiy bandalariga azob berishga\n" +
                "\n" +
                "shoshilmaydigan, faqat mavridi kelgandagina jazolovchi."
    ),
    AllahName(
        " Al-Azim",
        "Inson aqli, tafakkuri tasavvur qila olmaydigan darajada ulug‘ Zot."
    ),
    AllahName(
        "Al-G‘afur",
        "Bandalar gunohlarini kechirib, ayb-kamchiliklarini berkituvchi."
    ),
    AllahName(
        "Ash-Shakur",
        "Itoatkor, solih amallar qiluvchi bandalariga nihoyatda ko‘p mukofot beruvchi, oz amal qiluvchi\n" +
                "\n" +
                "bandalariga ham niyatlariga, ixloslariga yarasha ajr-savob beruvchi."
    ),
    AllahName(
        "Al-Aliy",
        "Juda oliy martabal, Uning zotini, sifatini tasavvur qilishga aqllar ojizlik qiladi."
    ),
    AllahName(
        "Al-Kabir",
        "O‘ta ulug‘, maxluqotlaridan mutlaq ustun, azaliy, abadiy Zot."
    ),
    AllahName(
        "Al-Hafiz",
        "Maxluqotlarini O‘zi xohlagan muddat halokatdan saqlab turuvchi."
    ),
    AllahName(
        "Al-Muqit",
        "“Al-Muqit” sifatining birinchi maʼnosi “Al-Hafiz”niki bilan bir xil. Ikkinchi maʼnosi esa\n" +
                "\n" +
                "har bir maxluqotiga o‘z nasibasini beruvchidir."
    ),
    AllahName(
        "Al-Hasib",
        "Kifoya qiluvchi, maxluqotlarini kifoya qiladigan darajada rizqlantiruvchi, qiyomat kuni\n" +
                "\n" +
                "bandalarini hisob qiluvchi."
    ),
    AllahName(
        "Al-Jalil",
        "Ulug‘lik sifatini o‘zida jamlagan Zo, buyuk, ulug‘, oliy."
    ),
    AllahName(
        "Al-Karim",
        "Karami, saxovati cheksiz Zot. Inʼom qilish bilan Allohning xazinasi kamayib qolmaydi."
    ),
    AllahName(
        "Ar-Raqib",
        "Har bir narsani kuzatib turuvchi."
    ),
    AllahName(
        "Al-Mujib",
        "Bandalar ixlos bilan qilgan duolarini ijobat etuvchi."
    ),
    AllahName(
        "Al-Vosiʼ",
        "Rahmati barcha narsadan keng, xohlagan bandasi rizqini kengaytiruvchi."
    ),
    AllahName(
        "Al-Hakim",
        "Har bir tadbirini hikmat bilan amalga oshiruvchi."
    ),
    AllahName(
        "Al-Vadud",
        "O‘zining solih bandalarini yaxshi ko‘ruvchi, ulardan rozi bo‘luvchi, bandalari tomonidan seviluvchi."
    ),
    AllahName(
        "Al-Majid",
        "Shuhrati nihoyatda cheksiz, qadri juda balan, karamining cheki yo‘q, o‘ta saxiy Zot."
    ),
    AllahName(
        "Al-Bois", "Mazkur sifatning ikki xil maʼnosi bor: 1. Tiriltiruvchi. 2. Yuboruvchi.\n" +
                "\n" +
                "Yaʼni, bandalariga payg‘ambarlar yuboruvchi, barcha maxluqotlarini qiyomat kuni qayta\n" +
                "\n" +
                "tiriltiruvchi."
    ),
    AllahName(
        "Ash-Shahid",
        "Bo‘layotgan har bir narsa, hodisa ustida hozir-shohid bo‘lib turuvchi. Biron narsa Allohning\n" +
                "\n" +
                "guvohligidan chetda qolmaydi."
    ),
    AllahName(
        "Al-Haq",
        "Mavjudligi haqiqatan tasdiqlangan, Haq, Haqni yuzaga chiqaruvchi Zot."
    ),
    AllahName(
        "Al-Vakil",
        "Bandalari ishlarini amalga oshiruvchi, ularga manfaat yetkazishga kafil Zot."
    ),
    AllahName(
        "Al-Qaviy",
        "Kuch-quvvat Egasi, biron amalni bajarishdan ojiz qolmaydigan Zot. Alloh mutlaq qudrat\n" +
                "\n" +
                "Sohibidir."
    ),
    AllahName(
        "Al-Matin",
        "O‘ta quvvatli, matonatli Zot. Biron ishni amalga oshirgach, Alloh charchamaydi, zaiflashib olmaydi."
    ),
    AllahName(
        "Al-Valiy",
        "Yordam beruvch, valiy bandalarini yaxshi ko‘ruvch, butun olamdagi maxluqotlari ishlarini\n" +
                "\n" +
                "boshqarib turuvchi Zot."
    ),
    AllahName(
        "Al-Hamid",
        "Har qanday holatda, har qanday zamonda hamdu sanoga eng munosib, bandalari tomonidan tinimsiz\n" +
                "\n" +
                "maqtaladigan Zot."
    ),
    AllahName(
        "Al-Muhsiy",
        "Har bir narsani O‘z ilmi bilan hisobga oluvchi, ilmi barcha narsani qamragan, har bir narsaning\n" +
                "\n" +
                "eng nozik jihatlarini ham, eʼtiborga molik tomonlarini ham inobatga oluvchi."
    ),
    AllahName(
        "Al-Mubdiʼ",
        "Har bir narsani o‘xshashi yo‘q darajada avvaldan Yaratuvchi, yo‘qdan bor qiluvchi."
    ),
    AllahName(
        "Al-Muid",
        "Maxluqotlarini o‘limga qaytaruvchi (yaʼni, o‘ldiruvchi), so‘ng qiyomat kuni ularni yana hayotga\n" +
                "\n" +
                "qaytaruvchi."
    ),
    AllahName(
        "Al-Muhyi",
        "Qiyomatda o‘liklarni qayta tiriltiruvchi, ularga jon ato etuvchi."
    ),
    AllahName(
        "Al-Mumit",
        "O‘limni yaratuvchi, xohlagan bandasi jonini xohlagan vaqtida oluvchi."
    ),
    AllahName(
        "Al-Hay", "Doim tirik, hech qachon o‘lmaydigan Zot.\n" +
                "\n" +
                "Boqiylik faqat U Zotga xos. O‘lim, foniylik esa maxluqotlarga xos. Bunday sifatlar Allohga\n" +
                "\n" +
                "nisbat berilmaydi. Alloh barhayotligi bandalar tirikligidan tubdan farq qiladi."
    ),
    AllahName(
        "Al-Qayyum",
        "O‘z-o‘zidan qoim bo‘luvchi, boshqalarni ham qoim qiluvchi, har bir narsa ustida guvoh bo‘luvchi."
    ),
    AllahName(
        "Al-Vojid",
        "Xohlagan narsasini xohlagan vaqtida topuvchi, hech qachon faqir bo‘lmaydigan darajada boy Zot."
    ),
    AllahName(
        "Al-Mojid",
        "Shon-shuhrat Egasi, qadri baland, karamli, saxiy Zot."
    ),
    AllahName(
        "Al-Vohid", "Yagona, Yolg‘iz, sherigi yo‘q Zot." +
                "Alloh taoloning azalda ham sherigi bo‘lmagan, bundan keyin ham bo‘lmaydi. U Zot o‘z sifatida,\n" +
                "\n" +
                "zotida, ilohlikda, ibodatga munosiblikda Yakkayu Yagonadir."
    ),
    AllahName(
        "As-Somad",
        "Mazkur sifat bir necha maʼnolarni anglatadi. Jumladan, “mutlaq Hokim”, “doim barhayot\n" +
                "\n" +
                "turuvchi”, “hech kimga hojati tushmaydigan, aksincha maxluqotlari hojatini ravo qiluvchi Zot”."
    ),
    AllahName(
        "Al-Qodir",
        "Har bir narsaga qodir Zot. Alloh xohlagan ishini qilishdan ojiz emas. Har qanday ish Unga oson."
    ),
    AllahName(
        "Al-Muqtadir",
        "Qudrati cheksiz, nihoyatda kuchli Zot."
    ),
    AllahName(
        "Al-Muqaddim",
        "Xohlagan narsasini oldinga surib, ularni o‘z joyiga qo‘yuvchi. Kim yoki nima oldinga surilishga\n" +
                "\n" +
                "munosib bo‘lsa, oldinga suradi."
    ),
    AllahName(
        "Al-Muaxxir",
        "Xohlagan narsasini ortga surib, ularni o‘z joyiga qo‘yuvchi. Kim yoki nima ortga surilishga\n" +
                "\n" +
                "munosib bo‘lsa, ortga suradi."
    ),
    AllahName(
        "Al-Avval", "Boshlanishining avvali yo‘q, barcha narsadan avval bo‘lgan Zot.\n" +
                "\n" +
                "Alloh taolo maxluqotlar yaratilmasidan oldin ham mavjud edi. Dunyodagi barcha narsalar,\n" +
                "\n" +
                "mavjudotlar “Al-Avval” sifatli Alloh tomonidan yaratilgan."
    ),
    AllahName(
        "Al-Oxir",
        "Maxluqotlar o‘lib ketganidan so‘ng ham boqiy qoluvchi, hech qachon o‘lmaydigan, mavjudligining\n" +
                "\n" +
                "oxiri yo‘q Zot."
    ),
    AllahName(
        "Az-Zohir",
        "Barcha narsadan ustun, oliy Zot. Atrof-muhitdagi narsalar, holatlar Allohning zohirligiga\n" +
                "\n" +
                "dalolat qiladi.\n" +
                "\n" +
                "Haqiqatan, aql yuritgan kishi Allohning borligini, Yakkayu Yagonaligini biladi. Zero, U Zotning\n" +
                "\n" +
                "mavjudligi ochiq-oydin ko‘rinib turadi."
    ),
    AllahName(
        "Al-Botin",
        "Maxluqotlar nazaridan berkingan, ularga ko‘rinmaydigan Zot. Chunonchi, ko‘z bilan Allohni bu\n" +
                "\n" +
                "dunyoda ko‘rib bo‘lmaydi."
    ),
    AllahName(
        "Al-Voliy",
        "Barcha narsaning Egasi, ularni tasarruf etuvchi Zot."
    ),
    AllahName(
        "Al-Mutaoliy",
        "Kofirlar U Zotga nisbatan ayb taqashlaridan, mo‘minlar hamdu sanolaridan oliy Zot.\n" +
                "\n" +
                "Yaʼni, kofir va mushriklarning Alloh taologa nisbatan noloyiq tuhmatlari U Zot shaʼniga putur\n" +
                "\n" +
                "yetkazmaydi. Mo‘minlar hamdu sanolari esa U Zot ulug‘ligini ziyoda qilmaydi. Chunonchi, bandalari\n" +
                "\n" +
                "tuhmatlari, hamdu sanolari Alloh taologa zarar ham, foyda ham keltirmaydi. U Zot bandalari\n" +
                "\n" +
                "qiladigan amallaridan, aytadigan gaplaridan Oliy va Behojatdir."
    ),
    AllahName(
        "Al-Bar",
        "Bandalariga cheksiz yaxshiliklar qiluvchi, lutf-karami, ehsonining cheki yo‘q Zot."
    ),
    AllahName(
        "At-Tavvob",
        "Bandalari ixlos bilan qilgan tavbalarini qabul qiluvchi."
    ),
    AllahName(
        "Al-Muntaqim",
        "G‘azabini qo‘zg‘agan bandalaridan intiqom (qasos) oluvchi, ularni jazolovchi. Lekin Allohning\n" +
                "\n" +
                "jazolashi zulm emas, adolatdir."
    ),
    AllahName(
        "Al-Afuv", "Bandalari gunohlarini kechirib yuboruvchi.\n" +
                "\n" +
                "Mazkur sifat maʼnosi “Al-G‘afur”nikidan ham kuchliroq. Zero “Al-G‘afur” bandalar gunohlarini\n" +
                "\n" +
                "berkitishni, “Al-Afuv” gunohlarni kechirishni, o‘chirib yuborishni anglatadi."
    ),
    AllahName(
        "Ar-Rauf",
        "O‘ta Mehribon, nihoyatda Shafqatli, Rahmli Zot."
    ),
    AllahName(
        "Molikul mulk",
        "Mulk egasi, ishlarni O‘zi xohlagan tarzda amalga oshiradi, Uning hukmiga qarshi boruvchi yo‘q,\n" +
                "\n" +
                "mutlaq tasarruf qiluvchi."
    ),
    AllahName(
        "Zul jaloli val ikrom",
        "Ulug‘lik va karam Egasi."
    ),
    AllahName(
        "Al-Muqsit", "Adolatli Zot.\n" +
                "\n" +
                "Alloh O‘z hukmida, jazo berishida, mahrum etishida adolatlidir. Bandalariga zulm qilmaydi,\n" +
                "\n" +
                "ularni gunohlariga yarasha jazolaydi, yaxshi ishlarini munosib taqdirlaydi."
    ),
    AllahName(
        "Al-Jomiʼ",
        "Maxluqotlarni hisob qilish uchun qiyomat kuni mahshargohga Jamlovchi."
    ),
    AllahName(
        "Al-G‘aniy",
        "Boy, Behojat Zot. Boshqalarning Allohga hojati tushadi, biroq Alloh hech kimga, hech narsaga\n" +
                "\n" +
                "muhtoj emas."
    ),
    AllahName(
        "Al-Mug‘niy", "Behojat-boy qiluvchi.\n" +
                "\n" +
                "Alloh bandalari orasidan kimni xohlasa, o‘shani boy-behojat qilib qo‘yadi."
    ),
    AllahName(
        "Al-Moniʼ",
        "O‘ziga itoat etuvchi mo‘min bandalarini har xil kulfatlardan, qiyinchiliklardan asrovchi, ularni\n" +
                "\n" +
                "balolardan qutqaruvch, xohlagan bandasidan keng rizqni man qiluvchi."
    ),
    AllahName(
        "Az-Zor", "Zararli narsalarni ham yaratuvchi.\n" +
                "\n" +
                "Alloh yaxshini ham, yomonni ham, foydalini ham, zararlini ham yaratadi. Shu orqali xohlagan\n" +
                "\n" +
                "maxluqotiga O‘z hikmati bilan zarar yetkazadi."
    ),
    AllahName(
        "An-Nofiʼ",
        "Xohlagan bandasiga manfaat keltiruvchi."
    ),
    AllahName(
        "An-Nur",
        "Ko‘zi ojizlar Allohning nuri bilan ko‘radi. Maʼnaviy so‘qirlar Uning hidoyati ila to‘g‘ri yo‘lni\n" +
                "\n" +
                "topadi. Alloh O‘zi zohir bo‘luvchi, o‘zgalarni ham zohir qiluvchi Zotdir. U osmonlar va Yerning nuridir."
    ),
    AllahName(
        "Al-Hodiy",
        "Xohlagan bandasini to‘g‘ri yo‘lga yo‘llovchi, hidoyatga boshlovchi."
    ),
    AllahName(
        "Al-Badiʼ",
        "Mislsiz narsalarni yo‘qdan bor qiluvchi."
    ),
    AllahName(
        "Al-Boqiy",
        "Doim boqiy turuvchi, foniylik sifatidan xoli."
    ),
    AllahName(
        "Al-Voris",
        "Barcha maxluqotlar o‘lib ketganidan keyin ham mangu qoluvchi."
    ),
    AllahName(
        "Ar-Rashid",
        "Xohlagan bandasini to‘g‘ri yo‘lga boshlovchi."
    ),
    AllahName(
        "As-Sabur", "O‘ta sabrli Zot.\n" +
                "\n" +
                "Alloh gunohkorlarni jazolashga shoshilmaydi, balki hidoyat yo‘liga yurib, o‘zlarini isloh\n" +
                "\n" +
                "qilishlariga imkon beradi."
    )
)