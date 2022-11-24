"""
=================================================================
Функции для получения писем и сведений о них
Main func: read_emails
=================================================================
"""

import os
import re
from email import policy

from conf import *

from imaplib import IMAP4_SSL
import email
from email.header import decode_header
import datetime
from log_config import *

from pymongo import MongoClient
import random, string


def create_mail_connection():
    """
    Создает соединение с почтовым сервером
    """
    logging.info("Creating connection with mail server...")
    mail_connection = IMAP4_SSL(host="imap.gmail.com")
    mail_connection.login(user=USER_EMAIL, password=USER_PASSWORD)
    logging.info(f"The connection was established. Details: {mail_connection}")
    return mail_connection


def get_mail_ids(folder: str, mail_connection):
    """
    Получает все id из папки писем
    """
    logging.info("Getting letters ids from folder...")
    logging.debug(f"folder: {folder}")
    logging.debug(f"mail_connection: {mail_connection}")
    mail_connection.select(folder, readonly=True)
    result, data = mail_connection.search(None, "ALL")
    ids = data[0].decode("utf-8")
    mail_ids = ids.split()
    logging.info(f"Letters IDs gathered. Details: {mail_ids}")
    return mail_ids


def filter_mails_id_by_date(mail_ids, mail_connection, start_dt, end_dt,
                            folder):
    """
    Фильтрует все id писем по дате
    """
    logging.info("Filtering letters ids by date...")
    logging.debug(f"mail_ids: {mail_ids}")
    logging.debug(f"start_dt: {start_dt}")
    logging.debug(f"end_dt: {end_dt}")
    logging.debug(f"folder: {folder}")

    mails_filter_by_data = []
    start_letter_id = find_id_letter(mail_ids=mail_ids,
                                     mail_connection=mail_connection,
                                     date=start_dt, folder=folder)
    end_letter_id = find_id_letter(mail_ids=mail_ids,
                                   mail_connection=mail_connection,
                                   date=end_dt, folder=folder, is_end=True)
    for i in range(start_letter_id + 1, end_letter_id + 1):
        mails_filter_by_data.append(str(i))

    logging.info(f"Filtering letters ids by date: {mails_filter_by_data}")

    return mails_filter_by_data


def find_id_letter(mail_ids, mail_connection, date, folder,
                   is_end: bool = False):
    """
    Реализует алгоритм быстрого поиска писем по дате
    """
    logging.info("Process of find letter id started...")
    logging.debug(f"mail_ids: {mail_ids}")
    logging.debug(f"date: {date}")
    logging.debug(f"folder: {folder}")
    logging.debug(f"is_end: {is_end}")

    start = 0
    end = len(mail_ids)
    step = 0
    while start < end:
        step = step + 1
        mid = (start + end) // 2
        date_mid = get_mail_date(mail_id=mail_ids[mid],
                                 mail_connection=mail_connection,
                                 folder=folder)
        if date_mid == date:
            return mid
        if date < date_mid:
            end = mid - 1
        else:
            start = mid + 1
    if start >= end and not is_end:
        return end + 1

    logging.info("Process of find letter id is finished")

    return mid + 1


def get_mail_date(mail_id, mail_connection, folder):
    """
    Получает дату письма
    """
    logging.info("Getting letter date...")
    logging.debug(f"folder: {folder}")
    logging.debug(f"mail_id: {mail_id}")

    mail_data = get_mail_data(mail_connection=mail_connection, mail_id=mail_id,
                              folder=folder)

    list_for_check_formats = [
        {'input_date': mail_data["Date"][:30], "from_format": "%a, %d %b %Y %H:%M:%S %z"},
        {'input_date': mail_data["Date"][:31], "from_format": "%a, %d %b %Y %H:%M:%S %z"},
        {'input_date': mail_data["Date"], "from_format": "%d %b %Y %H:%M:%S %z"},
        {'input_date': mail_data["Date"][:25], "from_format": "%a, %d %b %Y %H:%M:%S"},
    ]
    for case in list_for_check_formats:
        chek, dt_email = convert_date(case['input_date'], case['from_format'])
        if chek:
            break

    if dt_email == None:
        raise Exception('Форат даты письма не соответствует ни одному указанному шаблону даты')

    logging.info(f"Letter date: {dt_email}")

    return dt_email


def convert_date(date_text, from_format):
    """
    Получает строку даты из письма и пытается перевести ее из указанного формата в необходимый
    """
    try:
        return [True, datetime.datetime.strptime(date_text, from_format).strftime("%Y-%m-%d %H:%M:%S")]
    except:
        return [False, None]


def get_mail_body(mail_data):
    """
    Получает текст писем
    """
    logging.info("Getting bodies of letters...")
    logging.debug(f"mail_data: {mail_data}")

    while mail_data.is_multipart():
        mail_data = mail_data.get_payload(0)
        logging.debug(mail_data)
    try:
        content = mail_data.get_payload(decode=True).decode(encoding="utf-8")
        logging.debug("Mail body is: " + str(content))
        return content
    except Exception as e:
        logging.error(f"Problems getting letter body. Details: {e}")


def get_mail_subject(mail_data):
    """
    Получает заголовок письма
    """
    logging.info("Getting subjects of letters...")
    logging.debug(f"mail_data: {mail_data}")

    try:
        current_subject = mail_data.get("Subject")
        encoded_subject = decode_header(current_subject)[
            0][0].decode(encoding="utf-8")
        logging.info("Mail subject is: " + str(encoded_subject))
        return encoded_subject
    except Exception as e:
        logging.error(f"Problems getting letter subject. Details: {e}")


def get_mail_data(mail_connection, mail_id, folder):
    """
    Получает отправителя и получателя письма
    """
    logging.info("Getting mail data...")
    logging.debug(f"mail_id: {mail_id}")
    logging.debug(f"folder: {folder}")

    mail_connection.select(mailbox=folder, readonly=True)
    typ, data = mail_connection.fetch(message_set=mail_id,
                                      message_parts="(RFC822)")
    raw_email = data[0][1]
    raw_email_string = raw_email.decode(encoding="utf-8")
    email_message = email.message_from_string(raw_email_string)

    logging.debug(f"Email date gathered: {email_message}")

    return email_message


def get_mail_sender(mail_data):
    """
    Получает отправителя письма
    """
    logging.info("Getting mail sender...")
    logging.debug(f"mail_data: {mail_data}")

    sender = mail_data.get("From")

    logging.debug("Mail sender is: " + str(sender))

    return sender


def get_mail_reciever(mail_data):
    """
    Получает получателя письма
    """
    logging.info("Getting mail reciever...")
    logging.debug(f"mail_data: {mail_data}")
    # print(mail_data)
    reciever = mail_data.get("To")

    logging.debug("Mail reciever is: " + str(reciever))

    return reciever


def read_emails(folder, start_dt, end_dt):
    """
    Читает письма email
    """
    logging.info("Reading emails...")
    logging.debug(f"folder: {folder}")
    logging.debug(f"start_dt: {start_dt}")
    logging.debug(f"end_dt: {end_dt}")

    mail_connection = create_mail_connection()
    id_list = get_mail_ids(folder=folder, mail_connection=mail_connection)
    mails_id = filter_mails_id_by_date(mail_ids=id_list,
                                       mail_connection=mail_connection,
                                       start_dt=start_dt,
                                       end_dt=end_dt, folder=folder)
    mails = []
    for mail_id in mails_id:
        date = get_mail_date(mail_connection=mail_connection, mail_id=mail_id,
                             folder=folder)
        mail_data = get_mail_data(mail_connection=mail_connection,
                                  mail_id=mail_id, folder=folder)

        body = get_mail_body(mail_data=mail_data)
        subject = get_mail_subject(mail_data=mail_data)
        reciever = get_mail_reciever(mail_data=mail_data)
        sender = get_mail_sender(mail_data=mail_data)
        mail = {"mail_id": mail_id, "body": body, "subject": subject,
                "reciever": reciever, "sender": sender, "date": date, 'raw_data': mail_data, }
        mails.append(mail)
    mail_connection.close()

    logging.debug("Mails is: " + str(mails))

    return mails


def save_attachment(msg, download_folder="/tmp"):
    """
    Given a message, save its attachments to the specified
    download folder (default is /tmp)

    return: file path to attachment
    """
    att_paths = []
    for part in msg.walk():
        if part.get_content_maintype() == 'multipart':
            continue
        if part.get('Content-Disposition') is None:
            continue

        filename = part.get_filename()
        if filename is None:
            continue
        att_path = os.path.join(download_folder, filename)

        if os.path.isfile(att_path):
            att_path = os.path.join(download_folder, randomword(10) + '_' + filename)
        try:
            with open(att_path, 'wb') as fp:
                fp.write(part.get_payload(decode=True))
            att_paths.append(att_path)
        except:
            pass
    return att_paths


def randomword(length):
    letters = string.ascii_lowercase
    return ''.join(random.choice(letters) for i in range(length))


def strip_text_email(email_body):
    new_list = []
    count = 1
    row_list = email_body.split('\n')
    for row in row_list:
        match_attach = re.search(r'\[cid:.*\]', row)
        if match_attach:
            row = '[Вложение ' + str(count) + ']'
            count += 1
        match_end = re.search(LAST_ROW_OF_LETTER, row)
        if match_end:
            break
        if row != '':
            new_list.append(row)

    return '\n'.join(new_list)


def get_database_conn():
    # Provide the mongodb atlas url to connect python to mongodb using pymongo
    # Create a connection using MongoClient. You can import MongoClient or use pymongo.MongoClient
    client = MongoClient(MONGO_URI)
    db_conn = client[MONGO_DB_NAME]
    # Create the database for our example (we will use the same database throughout the tutorial
    return db_conn


def chek_subjects_in_db(set_subjects):
    db_conn = get_database_conn()
    collection_name = db_conn["mail_subjects"]

    # разделяем тему письма на jira код и название
    subject_dict = {}
    for subj in set_subjects:
        match = re.search(REGULAR_MASK_KEY, subj)
        subject_dict[subj] = {}
        if match is not None:
            subject_dict[subj]['key'] = match[0]
            them = subj.replace(match[0], '')
            them = them.replace(DEL_FROM_SUBJECT1, '')
            subject_dict[subj]['them'] = them.replace(DEL_FROM_SUBJECT2, '')
        else:
            subject_dict[subj]['key'] = subj
            subject_dict[subj]['them'] = None

    #проверяем key в бд
    new_set_subjects = set()
    for subj in set_subjects:
        key = subject_dict[subj]['key']
        item_details = collection_name.find({"key": key})
        try:
            item_details.next()
        except StopIteration:
            new_set_subjects.add(subj)
    return new_set_subjects, subject_dict

if '__main__' == __name__:
    time_now = datetime.datetime.now()
    start = str(time_now - datetime.timedelta(**PERIOD_OF_SCAN_LETTER))[:19]
    end = str(time_now)[:19]

    list_mails_data = read_emails(EMAIL_FOLDER, start, end)
    # исключаем получателей не нас
    list_mails_data = list(filter(lambda x:
                                  x["reciever"].lower().find(RECIEVER) != -1 and x["subject"] is not None,
                                  list_mails_data))
    list_mails_data = list(filter(lambda x:
                                  x['subject'].lower().find(IGNORE_MAIL_WITH_IN_SUBJECT) == -1,
                                  list_mails_data))

    # получаем уникальные названия тем писем
    set_subjects = set(map(lambda x: x['subject'], list_mails_data))


    set_subjects, subject_dict = chek_subjects_in_db(set_subjects)

    dict_topics = {}
    list_result = []
    for i in set_subjects:
        dict_topics[i] = list(filter(lambda x: x['subject'] == i, list_mails_data))
        email_text = strip_text_email(
            str(email.message_from_string(dict_topics[i][0]['body'], policy=policy.default))
        )

        dict_result = {}
        dict_result['key'] = subject_dict[i]['key']
        dict_result['subject'] = subject_dict[i]['them']
        dict_result['text'] = email_text
        dict_result['attachments'] = save_attachment(dict_topics[i][0]['raw_data'], download_folder=ATTACHMENTS_FILDER)
        dict_result['sended'] = 0
        list_result.append(dict_result)

    db_conn = get_database_conn()
    collection_name = db_conn[MONGO_TABLE_NAME]
    print(list_result)
    try:
        collection_name.insert_many(list_result)
    except TypeError:
        print('Новых писем нет')
    # mail = email.message_from_string(dict_result[i]['body'], policy=policy.default)
    # mail.get_body().get_payload(decode=True)


