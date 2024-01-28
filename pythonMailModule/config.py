PERIOD_OF_SCAN_LETTER = {"hours": 5}  # период, за который сканируются сообщения, может быть days, hours
ATTACHMENTS_FOLDER = r'attachments'  # путь до папки, куда сохранять вложения
MONGO_URI = "mongodb://login:passw@host:port/"  # на mm-service монго бд на 27000 порту
MONGO_DB_NAME = 'emailAlertBot_TEST'
# USER_PASSWORD - пароль "приложения" от почты, создается отдельно
# EMAIL_FOLDER - назвение папки/ярлыка на письма из которого триггериться, без пробелов


# REGULAR_MASK_KEY - по этой маске будет браться уникальный ключ темы,
# чтобы для одной темы письма не повторялись сообщения, если название темы слегка изменится
# Если маска не находит сходства, то за уникальный ключ берется название всей темы
# синтаксис регулярок python

# DEL_FROM_SUBJECT1 - удаляет из темы письма данную подстроку, если маска нашла соответствие
# DEL_FROM_SUBJECT2 - аналогично

# RECIEVER - почта получателя сообщений, письма на другую почту не обрабатываются,
# (в нижнем регистре), если указать просто @, то любой получатель
# ONLY_FIRST_MAIL - если True, то будет проверка, были ли уже обработаны письма с такой темой письма и удаляются ответы, если False, то проверка идет по id письма
# LAST_ROW_OF_LETTER - Текст писем будет обрезаться при встрече данной строчки
# RESTRICTED_SUBJECTS - письма с указанными подстроками в теме будут отсеяны


GPB = {
    'USER_EMAIL': "andrey.pervushin@glowbyteconsulting.com",
    'USER_PASSWORD': "passw",
    'EMAIL_FOLDER': '_GPB_SUPPORT',

    'HAVE_UNIC_KEY': True,
    'REGULAR_MASK_KEY': r'RTOPLN-\d+',
    'DEL_FROM_SUBJECT1': '| JIRA',
    'DEL_FROM_SUBJECT2': '|',

    'ONLY_FIRST_MAIL': True,
    'ENABLE_ASSIGN_TO_PEOPLE_IN_CHAT': True,
    'RECIEVER': '@glowbyteconsulting.com',

    'LAST_ROW_OF_LETTER': "С уважением,",

    'RESTRICTED_SUBJECTS': ["re:", ],
}


CONFIG_LIST = [GPB, ]
