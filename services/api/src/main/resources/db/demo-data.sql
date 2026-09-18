INSERT INTO integration.github_account (account_id, login, account_type) VALUES
 (9000001, 'acme-demo', 'ORGANIZATION') ON CONFLICT (account_id) DO NOTHING;
INSERT INTO integration.github_installation (installation_id, account_id, installed_at) VALUES
 (9000001, 9000001, CURRENT_TIMESTAMP - INTERVAL '1 year') ON CONFLICT (installation_id) DO NOTHING;
INSERT INTO evidence.repository (repository_id, installation_id, account_id, name, full_name, default_branch, private) VALUES
 (9000001, 9000001, 9000001, 'checkout-service', 'acme-demo/checkout-service', 'main', false)
 ON CONFLICT (repository_id) DO NOTHING;

DELETE FROM evidence.coverage_snapshot WHERE repository_id=9000001;
DELETE FROM analytics.ai_intervention WHERE repository_id=9000001;
DELETE FROM analytics.measurement_period WHERE repository_id=9000001;
DELETE FROM evidence.release WHERE repository_id=9000001;
DELETE FROM evidence.workflow_job WHERE repository_id=9000001;
DELETE FROM evidence.bug_file_link WHERE issue_id BETWEEN 9100001 AND 9100099;
DELETE FROM evidence.issue WHERE repository_id=9000001;
DELETE FROM evidence.file_change WHERE pull_request_id BETWEEN 9200001 AND 9200099;
DELETE FROM evidence.review_request WHERE pull_request_id BETWEEN 9200001 AND 9200099;
DELETE FROM evidence.pull_request_review WHERE pull_request_id BETWEEN 9200001 AND 9200099;
DELETE FROM evidence.pull_request WHERE repository_id=9000001;
DELETE FROM evidence.contributor WHERE account_id=9000001;

INSERT INTO evidence.contributor (contributor_id,account_id,login) VALUES
 (9300001,9000001,'demo-a'),(9300002,9000001,'demo-b'),(9300003,9000001,'demo-c'),(9300004,9000001,'demo-d');

INSERT INTO evidence.pull_request
 (pull_request_id,repository_id,number,author_id,state,opened_at,first_review_requested_at,merged_at,closed_at,additions,deletions,changed_files) VALUES
 (9200001,9000001,101,9300001,'MERGED',CURRENT_TIMESTAMP-INTERVAL '25 days',CURRENT_TIMESTAMP-INTERVAL '24 days 18 hours',CURRENT_TIMESTAMP-INTERVAL '23 days',CURRENT_TIMESTAMP-INTERVAL '23 days',120,30,5),
 (9200002,9000001,102,9300002,'MERGED',CURRENT_TIMESTAMP-INTERVAL '20 days',CURRENT_TIMESTAMP-INTERVAL '19 days 18 hours',CURRENT_TIMESTAMP-INTERVAL '18 days',CURRENT_TIMESTAMP-INTERVAL '18 days',480,140,18),
 (9200003,9000001,103,9300003,'MERGED',CURRENT_TIMESTAMP-INTERVAL '15 days',CURRENT_TIMESTAMP-INTERVAL '14 days 20 hours',CURRENT_TIMESTAMP-INTERVAL '14 days',CURRENT_TIMESTAMP-INTERVAL '14 days',45,12,3),
 (9200004,9000001,104,9300004,'MERGED',CURRENT_TIMESTAMP-INTERVAL '10 days',CURRENT_TIMESTAMP-INTERVAL '9 days 16 hours',CURRENT_TIMESTAMP-INTERVAL '7 days',CURRENT_TIMESTAMP-INTERVAL '7 days',820,260,28),
 (9200005,9000001,105,9300001,'OPEN',CURRENT_TIMESTAMP-INTERVAL '12 days',CURRENT_TIMESTAMP-INTERVAL '10 days',NULL,NULL,210,80,9),
 (9200006,9000001,106,9300002,'OPEN',CURRENT_TIMESTAMP-INTERVAL '4 days',CURRENT_TIMESTAMP-INTERVAL '3 days 18 hours',NULL,NULL,55,9,2),
 (9200007,9000001,107,9300003,'MERGED',CURRENT_TIMESTAMP-INTERVAL '6 days',CURRENT_TIMESTAMP-INTERVAL '5 days 20 hours',CURRENT_TIMESTAMP-INTERVAL '5 days',CURRENT_TIMESTAMP-INTERVAL '5 days',95,40,4),
 (9200008,9000001,108,9300004,'OPEN',CURRENT_TIMESTAMP-INTERVAL '1 day',CURRENT_TIMESTAMP-INTERVAL '18 hours',NULL,NULL,510,210,16);

INSERT INTO evidence.pull_request_review (review_id,pull_request_id,reviewer_id,state,submitted_at) VALUES
 (9400001,9200001,9300002,'APPROVED',CURRENT_TIMESTAMP-INTERVAL '24 days'),
 (9400002,9200002,9300003,'CHANGES_REQUESTED',CURRENT_TIMESTAMP-INTERVAL '19 days'),
 (9400003,9200003,9300004,'APPROVED',CURRENT_TIMESTAMP-INTERVAL '14 days 8 hours'),
 (9400004,9200004,9300002,'CHANGES_REQUESTED',CURRENT_TIMESTAMP-INTERVAL '9 days'),
 (9400005,9200007,9300001,'APPROVED',CURRENT_TIMESTAMP-INTERVAL '5 days 8 hours');
INSERT INTO evidence.review_request (pull_request_id,reviewer_id,requested_at,fulfilled_at) VALUES
 (9200001,9300002,CURRENT_TIMESTAMP-INTERVAL '24 days 18 hours',CURRENT_TIMESTAMP-INTERVAL '24 days'),
 (9200002,9300003,CURRENT_TIMESTAMP-INTERVAL '19 days 18 hours',CURRENT_TIMESTAMP-INTERVAL '19 days'),
 (9200003,9300004,CURRENT_TIMESTAMP-INTERVAL '14 days 20 hours',CURRENT_TIMESTAMP-INTERVAL '14 days 8 hours'),
 (9200004,9300002,CURRENT_TIMESTAMP-INTERVAL '9 days 16 hours',CURRENT_TIMESTAMP-INTERVAL '9 days'),
 (9200005,9300002,CURRENT_TIMESTAMP-INTERVAL '10 days',NULL),(9200006,9300002,CURRENT_TIMESTAMP-INTERVAL '3 days 18 hours',NULL),
 (9200007,9300001,CURRENT_TIMESTAMP-INTERVAL '5 days 20 hours',CURRENT_TIMESTAMP-INTERVAL '5 days 8 hours'),
 (9200008,9300002,CURRENT_TIMESTAMP-INTERVAL '18 hours',NULL);

INSERT INTO evidence.file_change (pull_request_id,path,additions,deletions) VALUES
 (9200001,'src/PaymentService.java',70,15),(9200001,'src/PaymentServiceTest.java',50,15),
 (9200002,'src/PaymentService.java',220,60),(9200002,'src/OrderController.java',180,50),(9200002,'db/migration.sql',80,30),
 (9200003,'src/OrderController.java',45,12),(9200004,'src/PaymentService.java',500,150),(9200004,'src/FraudClient.java',320,110),
 (9200005,'src/PaymentService.java',150,50),(9200005,'src/PaymentServiceTest.java',60,30),(9200006,'src/OrderController.java',55,9),
 (9200007,'src/FraudClient.java',95,40),(9200008,'src/PaymentService.java',310,120),(9200008,'src/OrderController.java',200,90);

INSERT INTO evidence.issue (issue_id,repository_id,number,state,created_at,closed_at,is_bug) VALUES
 (9100001,9000001,201,'OPEN',CURRENT_TIMESTAMP-INTERVAL '45 days',NULL,true),(9100002,9000001,202,'OPEN',CURRENT_TIMESTAMP-INTERVAL '18 days',NULL,false),
 (9100003,9000001,203,'OPEN',CURRENT_TIMESTAMP-INTERVAL '6 days',NULL,true),(9100004,9000001,204,'CLOSED',CURRENT_TIMESTAMP-INTERVAL '30 days',CURRENT_TIMESTAMP-INTERVAL '20 days',true),
 (9100005,9000001,205,'CLOSED',CURRENT_TIMESTAMP-INTERVAL '12 days',CURRENT_TIMESTAMP-INTERVAL '8 days',true),(9100006,9000001,206,'OPEN',CURRENT_TIMESTAMP-INTERVAL '2 days',NULL,false);
INSERT INTO evidence.bug_file_link (issue_id,path,linkage_method) VALUES
 (9100001,'src/PaymentService.java','FIXING_PR'),(9100003,'src/FraudClient.java','FIXING_PR'),
 (9100004,'src/PaymentService.java','FIXING_PR'),(9100005,'src/OrderController.java','FIXING_PR');

INSERT INTO evidence.workflow_job (workflow_job_id,repository_id,workflow_name,job_name,head_sha,attempt,started_at,completed_at,conclusion) VALUES
 (9500001,9000001,'build','unit-tests','aaa111',1,CURRENT_TIMESTAMP-INTERVAL '8 days',CURRENT_TIMESTAMP-INTERVAL '8 days'+INTERVAL '8 minutes','FAILURE'),
 (9500002,9000001,'build','unit-tests','aaa111',2,CURRENT_TIMESTAMP-INTERVAL '8 days'+INTERVAL '10 minutes',CURRENT_TIMESTAMP-INTERVAL '8 days'+INTERVAL '18 minutes','SUCCESS'),
 (9500003,9000001,'build','integration-tests','bbb222',1,CURRENT_TIMESTAMP-INTERVAL '5 days',CURRENT_TIMESTAMP-INTERVAL '5 days'+INTERVAL '14 minutes','SUCCESS'),
 (9500004,9000001,'build','unit-tests','ccc333',1,CURRENT_TIMESTAMP-INTERVAL '2 days',CURRENT_TIMESTAMP-INTERVAL '2 days'+INTERVAL '7 minutes','SUCCESS');

INSERT INTO evidence.release (release_id,repository_id,tag_name,published_at) VALUES
 (9600001,9000001,'v1.3.0',CURRENT_TIMESTAMP-INTERVAL '70 days'),(9600002,9000001,'v1.4.0',CURRENT_TIMESTAMP-INTERVAL '42 days'),
 (9600003,9000001,'v1.5.0',CURRENT_TIMESTAMP-INTERVAL '18 days'),(9600004,9000001,'v1.6.0',CURRENT_TIMESTAMP-INTERVAL '3 days');
INSERT INTO evidence.coverage_snapshot (repository_id,path,format,line_coverage,captured_at) VALUES
 (9000001,'src/PaymentService.java','JACOCO',.62,CURRENT_TIMESTAMP-INTERVAL '1 day'),
 (9000001,'src/OrderController.java','JACOCO',.84,CURRENT_TIMESTAMP-INTERVAL '1 day'),
 (9000001,'src/FraudClient.java','JACOCO',.41,CURRENT_TIMESTAMP-INTERVAL '1 day');

INSERT INTO analytics.measurement_period (period_id,repository_id,label,period_kind,starts_on,ends_on) VALUES
 (9700001,9000001,'Four weeks before pilot','BASELINE',CURRENT_DATE-INTERVAL '8 weeks',CURRENT_DATE-INTERVAL '4 weeks'),
 (9700002,9000001,'Four weeks after pilot','CURRENT',CURRENT_DATE-INTERVAL '4 weeks',CURRENT_DATE);
INSERT INTO analytics.metric_observation
 (period_id,metric_key,display_name,category,metric_value,unit,improvement_direction) VALUES
 (9700001,'test_coverage','Test coverage','QUALITY',62,'percent','HIGHER'),
 (9700002,'test_coverage','Test coverage','QUALITY',76,'percent','HIGHER'),
 (9700001,'rework_rate','Rework','EFFORT',14,'percent','LOWER'),
 (9700002,'rework_rate','Rework','EFFORT',9,'percent','LOWER'),
 (9700001,'cycle_time','PR cycle time','SPEED',52,'hours','LOWER'),
 (9700002,'cycle_time','PR cycle time','SPEED',48,'hours','LOWER'),
 (9700001,'review_wait','Review wait','FLOW',20,'hours','LOWER'),
 (9700002,'review_wait','Review wait','FLOW',12,'hours','LOWER');
INSERT INTO analytics.ai_adoption_snapshot (period_id,capability,adoption_percent,evidence_method) VALUES
 (9700002,'CODING',65,'Synthetic team survey'),(9700002,'TESTING',25,'Synthetic team survey'),
 (9700002,'PR_REVIEW',20,'Synthetic team survey'),(9700002,'DOCUMENTATION',45,'Synthetic team survey');
INSERT INTO analytics.ai_intervention (intervention_id,repository_id,capability,title,started_on,ended_on,status) VALUES
 (9800001,9000001,'TESTING','Approved testing-agent pilot',CURRENT_DATE-INTERVAL '4 weeks',CURRENT_DATE,'COMPLETED');
