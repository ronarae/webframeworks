INSERT INTO AEVENT(ID, IS_TICKETED, END_DATE, ENTRANCE_FEE, MAX_PARTICIPANTS, START_DATE, STATUS, TITLE) VALUES (20001, true, '20201111',30.00, 5, '20201112',1, 'Fantastic Event')
INSERT INTO AEVENT(ID, IS_TICKETED, END_DATE, ENTRANCE_FEE, MAX_PARTICIPANTS, START_DATE, STATUS, TITLE) VALUES (20002, true, '20201121',50.00, 50, '20201231',2, 'Fantastic Event')
INSERT INTO AEVENT(ID, IS_TICKETED, END_DATE, ENTRANCE_FEE, MAX_PARTICIPANTS, START_DATE, STATUS, TITLE) VALUES (20003, true, '20201101',30.00, 20, '20201113',1, 'Fantastic Event')
INSERT INTO AEVENT(ID, IS_TICKETED, END_DATE, ENTRANCE_FEE, MAX_PARTICIPANTS, START_DATE, STATUS, TITLE) VALUES (20004, false , '20201101',33.00, 20, '20201109',1, 'Fantastic Event')


INSERT INTO REGISTRATION(ID,TICKET_CODE, PAID, SUBMISSION_DATE, A_EVENT_ID) VALUES (1, '12345', false, '20201112', 20001)
INSERT INTO REGISTRATION(ID,TICKET_CODE, PAID, SUBMISSION_DATE, A_EVENT_ID) VALUES (2, '16543', false, '20201201', 20001)
INSERT INTO REGISTRATION(ID,TICKET_CODE, PAID, SUBMISSION_DATE, A_EVENT_ID) VALUES (3,'11111', true, '20201116', 20003)
INSERT INTO REGISTRATION(ID,TICKET_CODE, PAID, SUBMISSION_DATE, A_EVENT_ID) VALUES (4,'11111', true, '20201116', 20003)
INSERT INTO REGISTRATION(ID,TICKET_CODE, PAID, SUBMISSION_DATE, A_EVENT_ID) VALUES (5,'22222', false, '20201109', 20003)
INSERT INTO REGISTRATION(ID,TICKET_CODE, PAID, SUBMISSION_DATE, A_EVENT_ID) VALUES (6,'12332', true, '20201230', 20003)