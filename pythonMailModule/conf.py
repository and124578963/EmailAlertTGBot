USER_EMAIL = "andrey.pervushin@glowbyteconsulting.com"
USER_PASSWORD = "rsuzuzasdasdv"  # пароль "приложения" от почты
EMAIL_FOLDER = '_GPB_SUPPORT' # назвение папки/ярлыка на письма из которого триггериться

MONGO_URI = "mongodb://localhost:27000/"
MONGO_DB_NAME = 'GPB_TRACKS'

REGULAR_MASK_KEY = r'RTOPLN-\d+' # по этой маске будет браться уникальный ключ темы,
# чтобы для одной темы письма не повторялись сообщения, если название темы слегка изменится
# Если маска не находит сходства, то за уникальный ключ берется название всей темы
DEL_FROM_SUBJECT1 = '| JIRA' # удаляет из темы письма данную подстроку, если маска нашла соответствие
DEL_FROM_SUBJECT2 ='|' # аналогично

RECIEVER = '@glowbyteconsulting.com' # почта получателя сообщений, письма на другую почту не обрабатываются,
# в нижнем регистре
IGNORE_MAIL_WITH_IN_SUBJECT = 're:' #Письма, содержащие данну подстроку обрабатываться не будут,в нижнем регистре
ATTACHMENTS_FILDER = r'attachments' # путь куда складывать вложения в письма: картинки/логи

LAST_ROW_OF_LETTER = "С уважением," #Текст писем будет обрезаться при встрече данной строчки

PERIOD_OF_SCAN_LETTER = {"hours": 5} # период, за который сканируются сообщения, может быть days, hours



