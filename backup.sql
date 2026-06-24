--
-- PostgreSQL database dump
--

\restrict RpdY4Mama83w77xhAr0Yj8MaJnIcjCz829I3w3Na9um7xY7TwseCiaj3bE9abgm

-- Dumped from database version 17.10 (Debian 17.10-1.pgdg13+1)
-- Dumped by pg_dump version 17.10 (Debian 17.10-1.pgdg13+1)

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET transaction_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- Name: attachments; Type: TABLE; Schema: public; Owner: workflowuser
--

CREATE TABLE public.attachments (
    id bigint NOT NULL,
    content_type character varying(100) NOT NULL,
    created_at timestamp(6) without time zone NOT NULL,
    file_name character varying(255) NOT NULL,
    file_path character varying(500) NOT NULL,
    file_size bigint NOT NULL,
    workflow_request_id bigint NOT NULL
);


ALTER TABLE public.attachments OWNER TO workflowuser;

--
-- Name: attachments_id_seq; Type: SEQUENCE; Schema: public; Owner: workflowuser
--

CREATE SEQUENCE public.attachments_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.attachments_id_seq OWNER TO workflowuser;

--
-- Name: attachments_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: workflowuser
--

ALTER SEQUENCE public.attachments_id_seq OWNED BY public.attachments.id;


--
-- Name: audit_logs; Type: TABLE; Schema: public; Owner: workflowuser
--

CREATE TABLE public.audit_logs (
    id bigint NOT NULL,
    action character varying(100) NOT NULL,
    created_at timestamp(6) without time zone NOT NULL,
    details text,
    entity_id bigint NOT NULL,
    entity_type character varying(100) NOT NULL,
    performed_by_id bigint
);


ALTER TABLE public.audit_logs OWNER TO workflowuser;

--
-- Name: audit_logs_id_seq; Type: SEQUENCE; Schema: public; Owner: workflowuser
--

CREATE SEQUENCE public.audit_logs_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.audit_logs_id_seq OWNER TO workflowuser;

--
-- Name: audit_logs_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: workflowuser
--

ALTER SEQUENCE public.audit_logs_id_seq OWNED BY public.audit_logs.id;


--
-- Name: flyway_schema_history; Type: TABLE; Schema: public; Owner: workflowuser
--

CREATE TABLE public.flyway_schema_history (
    installed_rank integer NOT NULL,
    version character varying(50),
    description character varying(200) NOT NULL,
    type character varying(20) NOT NULL,
    script character varying(1000) NOT NULL,
    checksum integer,
    installed_by character varying(100) NOT NULL,
    installed_on timestamp without time zone DEFAULT now() NOT NULL,
    execution_time integer NOT NULL,
    success boolean NOT NULL
);


ALTER TABLE public.flyway_schema_history OWNER TO workflowuser;

--
-- Name: password_reset_tokens; Type: TABLE; Schema: public; Owner: workflowuser
--

CREATE TABLE public.password_reset_tokens (
    id bigint NOT NULL,
    created_at timestamp(6) without time zone,
    expiry_date timestamp(6) without time zone,
    token character varying(255) NOT NULL,
    used boolean NOT NULL,
    user_id bigint
);


ALTER TABLE public.password_reset_tokens OWNER TO workflowuser;

--
-- Name: password_reset_tokens_id_seq; Type: SEQUENCE; Schema: public; Owner: workflowuser
--

CREATE SEQUENCE public.password_reset_tokens_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.password_reset_tokens_id_seq OWNER TO workflowuser;

--
-- Name: password_reset_tokens_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: workflowuser
--

ALTER SEQUENCE public.password_reset_tokens_id_seq OWNED BY public.password_reset_tokens.id;


--
-- Name: refresh_tokens; Type: TABLE; Schema: public; Owner: workflowuser
--

CREATE TABLE public.refresh_tokens (
    id bigint NOT NULL,
    created_at timestamp(6) without time zone,
    expiry_date timestamp(6) without time zone,
    revoked boolean,
    token character varying(255) NOT NULL,
    user_id bigint
);


ALTER TABLE public.refresh_tokens OWNER TO workflowuser;

--
-- Name: refresh_tokens_id_seq; Type: SEQUENCE; Schema: public; Owner: workflowuser
--

CREATE SEQUENCE public.refresh_tokens_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.refresh_tokens_id_seq OWNER TO workflowuser;

--
-- Name: refresh_tokens_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: workflowuser
--

ALTER SEQUENCE public.refresh_tokens_id_seq OWNED BY public.refresh_tokens.id;


--
-- Name: roles; Type: TABLE; Schema: public; Owner: workflowuser
--

CREATE TABLE public.roles (
    id bigint NOT NULL,
    name character varying(255) NOT NULL,
    CONSTRAINT roles_name_check CHECK (((name)::text = ANY ((ARRAY['ROLE_ADMIN'::character varying, 'ROLE_APPROVER'::character varying, 'ROLE_REVIEWER'::character varying, 'ROLE_REQUESTER'::character varying])::text[])))
);


ALTER TABLE public.roles OWNER TO workflowuser;

--
-- Name: roles_id_seq; Type: SEQUENCE; Schema: public; Owner: workflowuser
--

CREATE SEQUENCE public.roles_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.roles_id_seq OWNER TO workflowuser;

--
-- Name: roles_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: workflowuser
--

ALTER SEQUENCE public.roles_id_seq OWNED BY public.roles.id;


--
-- Name: user_roles; Type: TABLE; Schema: public; Owner: workflowuser
--

CREATE TABLE public.user_roles (
    user_id bigint NOT NULL,
    role_id bigint NOT NULL
);


ALTER TABLE public.user_roles OWNER TO workflowuser;

--
-- Name: users; Type: TABLE; Schema: public; Owner: workflowuser
--

CREATE TABLE public.users (
    id bigint NOT NULL,
    created_at timestamp(6) without time zone NOT NULL,
    email character varying(255),
    enabled boolean,
    name character varying(255),
    password character varying(64) NOT NULL,
    token_version integer DEFAULT 0 NOT NULL,
    updated_at timestamp(6) without time zone,
    username character varying(255),
    phone character varying(20),
    address character varying(20)
);


ALTER TABLE public.users OWNER TO workflowuser;

--
-- Name: users_id_seq; Type: SEQUENCE; Schema: public; Owner: workflowuser
--

CREATE SEQUENCE public.users_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.users_id_seq OWNER TO workflowuser;

--
-- Name: users_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: workflowuser
--

ALTER SEQUENCE public.users_id_seq OWNED BY public.users.id;


--
-- Name: workflow_comment; Type: TABLE; Schema: public; Owner: workflowuser
--

CREATE TABLE public.workflow_comment (
    id bigint NOT NULL,
    comment character varying(255) NOT NULL,
    created_at timestamp(6) without time zone,
    deleted_at timestamp(6) without time zone,
    commenter_id bigint NOT NULL,
    workflow_instance_id bigint NOT NULL
);


ALTER TABLE public.workflow_comment OWNER TO workflowuser;

--
-- Name: workflow_comment_id_seq; Type: SEQUENCE; Schema: public; Owner: workflowuser
--

CREATE SEQUENCE public.workflow_comment_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.workflow_comment_id_seq OWNER TO workflowuser;

--
-- Name: workflow_comment_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: workflowuser
--

ALTER SEQUENCE public.workflow_comment_id_seq OWNED BY public.workflow_comment.id;


--
-- Name: workflow_histories; Type: TABLE; Schema: public; Owner: workflowuser
--

CREATE TABLE public.workflow_histories (
    id bigint NOT NULL,
    action character varying(255) NOT NULL,
    changed_at timestamp(6) without time zone NOT NULL,
    new_status character varying(255) NOT NULL,
    previous_status character varying(255),
    changed_by_id bigint,
    workflow_request_id bigint NOT NULL,
    CONSTRAINT workflow_histories_action_check CHECK (((action)::text = ANY ((ARRAY['SUBMITTED'::character varying, 'REVIEWED'::character varying, 'APPROVED'::character varying, 'REJECTED'::character varying])::text[]))),
    CONSTRAINT workflow_histories_new_status_check CHECK (((new_status)::text = ANY ((ARRAY['DRAFT'::character varying, 'PENDING'::character varying, 'UNDER_REVIEW'::character varying, 'APPROVED'::character varying, 'REJECTED'::character varying])::text[]))),
    CONSTRAINT workflow_histories_previous_status_check CHECK (((previous_status)::text = ANY ((ARRAY['DRAFT'::character varying, 'PENDING'::character varying, 'UNDER_REVIEW'::character varying, 'APPROVED'::character varying, 'REJECTED'::character varying])::text[])))
);


ALTER TABLE public.workflow_histories OWNER TO workflowuser;

--
-- Name: workflow_histories_id_seq; Type: SEQUENCE; Schema: public; Owner: workflowuser
--

CREATE SEQUENCE public.workflow_histories_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.workflow_histories_id_seq OWNER TO workflowuser;

--
-- Name: workflow_histories_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: workflowuser
--

ALTER SEQUENCE public.workflow_histories_id_seq OWNED BY public.workflow_histories.id;


--
-- Name: workflow_requests; Type: TABLE; Schema: public; Owner: workflowuser
--

CREATE TABLE public.workflow_requests (
    id bigint NOT NULL,
    approved_at timestamp(6) without time zone,
    created_at timestamp(6) without time zone NOT NULL,
    description text,
    rejected_at timestamp(6) without time zone,
    reviewed_at timestamp(6) without time zone,
    status character varying(255) NOT NULL,
    title character varying(255) NOT NULL,
    updated_at timestamp(6) without time zone NOT NULL,
    approved_by_id bigint,
    assigned_approver_id bigint,
    assigned_reviewer_id bigint,
    rejected_by_id bigint,
    reviewed_by_id bigint,
    submitted_by_id bigint NOT NULL,
    CONSTRAINT workflow_requests_status_check CHECK (((status)::text = ANY ((ARRAY['DRAFT'::character varying, 'PENDING'::character varying, 'UNDER_REVIEW'::character varying, 'APPROVED'::character varying, 'REJECTED'::character varying])::text[])))
);


ALTER TABLE public.workflow_requests OWNER TO workflowuser;

--
-- Name: workflow_requests_id_seq; Type: SEQUENCE; Schema: public; Owner: workflowuser
--

CREATE SEQUENCE public.workflow_requests_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.workflow_requests_id_seq OWNER TO workflowuser;

--
-- Name: workflow_requests_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: workflowuser
--

ALTER SEQUENCE public.workflow_requests_id_seq OWNED BY public.workflow_requests.id;


--
-- Name: attachments id; Type: DEFAULT; Schema: public; Owner: workflowuser
--

ALTER TABLE ONLY public.attachments ALTER COLUMN id SET DEFAULT nextval('public.attachments_id_seq'::regclass);


--
-- Name: audit_logs id; Type: DEFAULT; Schema: public; Owner: workflowuser
--

ALTER TABLE ONLY public.audit_logs ALTER COLUMN id SET DEFAULT nextval('public.audit_logs_id_seq'::regclass);


--
-- Name: password_reset_tokens id; Type: DEFAULT; Schema: public; Owner: workflowuser
--

ALTER TABLE ONLY public.password_reset_tokens ALTER COLUMN id SET DEFAULT nextval('public.password_reset_tokens_id_seq'::regclass);


--
-- Name: refresh_tokens id; Type: DEFAULT; Schema: public; Owner: workflowuser
--

ALTER TABLE ONLY public.refresh_tokens ALTER COLUMN id SET DEFAULT nextval('public.refresh_tokens_id_seq'::regclass);


--
-- Name: roles id; Type: DEFAULT; Schema: public; Owner: workflowuser
--

ALTER TABLE ONLY public.roles ALTER COLUMN id SET DEFAULT nextval('public.roles_id_seq'::regclass);


--
-- Name: users id; Type: DEFAULT; Schema: public; Owner: workflowuser
--

ALTER TABLE ONLY public.users ALTER COLUMN id SET DEFAULT nextval('public.users_id_seq'::regclass);


--
-- Name: workflow_comment id; Type: DEFAULT; Schema: public; Owner: workflowuser
--

ALTER TABLE ONLY public.workflow_comment ALTER COLUMN id SET DEFAULT nextval('public.workflow_comment_id_seq'::regclass);


--
-- Name: workflow_histories id; Type: DEFAULT; Schema: public; Owner: workflowuser
--

ALTER TABLE ONLY public.workflow_histories ALTER COLUMN id SET DEFAULT nextval('public.workflow_histories_id_seq'::regclass);


--
-- Name: workflow_requests id; Type: DEFAULT; Schema: public; Owner: workflowuser
--

ALTER TABLE ONLY public.workflow_requests ALTER COLUMN id SET DEFAULT nextval('public.workflow_requests_id_seq'::regclass);


--
-- Data for Name: attachments; Type: TABLE DATA; Schema: public; Owner: workflowuser
--

COPY public.attachments (id, content_type, created_at, file_name, file_path, file_size, workflow_request_id) FROM stdin;
\.


--
-- Data for Name: audit_logs; Type: TABLE DATA; Schema: public; Owner: workflowuser
--

COPY public.audit_logs (id, action, created_at, details, entity_id, entity_type, performed_by_id) FROM stdin;
\.


--
-- Data for Name: flyway_schema_history; Type: TABLE DATA; Schema: public; Owner: workflowuser
--

COPY public.flyway_schema_history (installed_rank, version, description, type, script, checksum, installed_by, installed_on, execution_time, success) FROM stdin;
1	1	<< Flyway Baseline >>	BASELINE	<< Flyway Baseline >>	\N	workflowuser	2026-06-18 15:11:04.99029	0	t
2	2	add phone to users	SQL	V2__add_phone_to_users.sql	21902330	workflowuser	2026-06-18 15:47:11.561783	17	t
3	3	add address to users	SQL	V3__add_address_to_users.sql	-1614026858	workflowuser	2026-06-18 15:50:11.365245	9	t
\.


--
-- Data for Name: password_reset_tokens; Type: TABLE DATA; Schema: public; Owner: workflowuser
--

COPY public.password_reset_tokens (id, created_at, expiry_date, token, used, user_id) FROM stdin;
\.


--
-- Data for Name: refresh_tokens; Type: TABLE DATA; Schema: public; Owner: workflowuser
--

COPY public.refresh_tokens (id, created_at, expiry_date, revoked, token, user_id) FROM stdin;
1	2026-06-15 14:45:38.726352	2045-08-14 14:45:38.725503	f	e8c837fc-33a6-4e5a-ad99-309f71af531d	1
\.


--
-- Data for Name: roles; Type: TABLE DATA; Schema: public; Owner: workflowuser
--

COPY public.roles (id, name) FROM stdin;
1	ROLE_ADMIN
2	ROLE_APPROVER
3	ROLE_REVIEWER
4	ROLE_REQUESTER
\.


--
-- Data for Name: user_roles; Type: TABLE DATA; Schema: public; Owner: workflowuser
--

COPY public.user_roles (user_id, role_id) FROM stdin;
1	4
\.


--
-- Data for Name: users; Type: TABLE DATA; Schema: public; Owner: workflowuser
--

COPY public.users (id, created_at, email, enabled, name, password, token_version, updated_at, username, phone, address) FROM stdin;
1	2026-06-15 14:45:04.658525	bulo@gmail.com	t	\N	$2a$10$W.LMTwaPgLa.nz4aPMb5vuOL8AS7psNFrC6Nf4UZt/Y4xhS0fjxI2	0	\N	bulo	\N	\N
\.


--
-- Data for Name: workflow_comment; Type: TABLE DATA; Schema: public; Owner: workflowuser
--

COPY public.workflow_comment (id, comment, created_at, deleted_at, commenter_id, workflow_instance_id) FROM stdin;
\.


--
-- Data for Name: workflow_histories; Type: TABLE DATA; Schema: public; Owner: workflowuser
--

COPY public.workflow_histories (id, action, changed_at, new_status, previous_status, changed_by_id, workflow_request_id) FROM stdin;
\.


--
-- Data for Name: workflow_requests; Type: TABLE DATA; Schema: public; Owner: workflowuser
--

COPY public.workflow_requests (id, approved_at, created_at, description, rejected_at, reviewed_at, status, title, updated_at, approved_by_id, assigned_approver_id, assigned_reviewer_id, rejected_by_id, reviewed_by_id, submitted_by_id) FROM stdin;
\.


--
-- Name: attachments_id_seq; Type: SEQUENCE SET; Schema: public; Owner: workflowuser
--

SELECT pg_catalog.setval('public.attachments_id_seq', 1, false);


--
-- Name: audit_logs_id_seq; Type: SEQUENCE SET; Schema: public; Owner: workflowuser
--

SELECT pg_catalog.setval('public.audit_logs_id_seq', 1, false);


--
-- Name: password_reset_tokens_id_seq; Type: SEQUENCE SET; Schema: public; Owner: workflowuser
--

SELECT pg_catalog.setval('public.password_reset_tokens_id_seq', 1, false);


--
-- Name: refresh_tokens_id_seq; Type: SEQUENCE SET; Schema: public; Owner: workflowuser
--

SELECT pg_catalog.setval('public.refresh_tokens_id_seq', 1, true);


--
-- Name: roles_id_seq; Type: SEQUENCE SET; Schema: public; Owner: workflowuser
--

SELECT pg_catalog.setval('public.roles_id_seq', 4, true);


--
-- Name: users_id_seq; Type: SEQUENCE SET; Schema: public; Owner: workflowuser
--

SELECT pg_catalog.setval('public.users_id_seq', 1, true);


--
-- Name: workflow_comment_id_seq; Type: SEQUENCE SET; Schema: public; Owner: workflowuser
--

SELECT pg_catalog.setval('public.workflow_comment_id_seq', 1, false);


--
-- Name: workflow_histories_id_seq; Type: SEQUENCE SET; Schema: public; Owner: workflowuser
--

SELECT pg_catalog.setval('public.workflow_histories_id_seq', 1, false);


--
-- Name: workflow_requests_id_seq; Type: SEQUENCE SET; Schema: public; Owner: workflowuser
--

SELECT pg_catalog.setval('public.workflow_requests_id_seq', 1, false);


--
-- Name: attachments attachments_pkey; Type: CONSTRAINT; Schema: public; Owner: workflowuser
--

ALTER TABLE ONLY public.attachments
    ADD CONSTRAINT attachments_pkey PRIMARY KEY (id);


--
-- Name: audit_logs audit_logs_pkey; Type: CONSTRAINT; Schema: public; Owner: workflowuser
--

ALTER TABLE ONLY public.audit_logs
    ADD CONSTRAINT audit_logs_pkey PRIMARY KEY (id);


--
-- Name: flyway_schema_history flyway_schema_history_pk; Type: CONSTRAINT; Schema: public; Owner: workflowuser
--

ALTER TABLE ONLY public.flyway_schema_history
    ADD CONSTRAINT flyway_schema_history_pk PRIMARY KEY (installed_rank);


--
-- Name: password_reset_tokens password_reset_tokens_pkey; Type: CONSTRAINT; Schema: public; Owner: workflowuser
--

ALTER TABLE ONLY public.password_reset_tokens
    ADD CONSTRAINT password_reset_tokens_pkey PRIMARY KEY (id);


--
-- Name: refresh_tokens refresh_tokens_pkey; Type: CONSTRAINT; Schema: public; Owner: workflowuser
--

ALTER TABLE ONLY public.refresh_tokens
    ADD CONSTRAINT refresh_tokens_pkey PRIMARY KEY (id);


--
-- Name: roles roles_pkey; Type: CONSTRAINT; Schema: public; Owner: workflowuser
--

ALTER TABLE ONLY public.roles
    ADD CONSTRAINT roles_pkey PRIMARY KEY (id);


--
-- Name: users uk_6dotkott2kjsp8vw4d0m25fb7; Type: CONSTRAINT; Schema: public; Owner: workflowuser
--

ALTER TABLE ONLY public.users
    ADD CONSTRAINT uk_6dotkott2kjsp8vw4d0m25fb7 UNIQUE (email);


--
-- Name: password_reset_tokens uk_71lqwbwtklmljk3qlsugr1mig; Type: CONSTRAINT; Schema: public; Owner: workflowuser
--

ALTER TABLE ONLY public.password_reset_tokens
    ADD CONSTRAINT uk_71lqwbwtklmljk3qlsugr1mig UNIQUE (token);


--
-- Name: refresh_tokens uk_ghpmfn23vmxfu3spu3lfg4r2d; Type: CONSTRAINT; Schema: public; Owner: workflowuser
--

ALTER TABLE ONLY public.refresh_tokens
    ADD CONSTRAINT uk_ghpmfn23vmxfu3spu3lfg4r2d UNIQUE (token);


--
-- Name: password_reset_tokens uk_la2ts67g4oh2sreayswhox1i6; Type: CONSTRAINT; Schema: public; Owner: workflowuser
--

ALTER TABLE ONLY public.password_reset_tokens
    ADD CONSTRAINT uk_la2ts67g4oh2sreayswhox1i6 UNIQUE (user_id);


--
-- Name: roles uk_ofx66keruapi6vyqpv6f2or37; Type: CONSTRAINT; Schema: public; Owner: workflowuser
--

ALTER TABLE ONLY public.roles
    ADD CONSTRAINT uk_ofx66keruapi6vyqpv6f2or37 UNIQUE (name);


--
-- Name: users uk_r43af9ap4edm43mmtq01oddj6; Type: CONSTRAINT; Schema: public; Owner: workflowuser
--

ALTER TABLE ONLY public.users
    ADD CONSTRAINT uk_r43af9ap4edm43mmtq01oddj6 UNIQUE (username);


--
-- Name: user_roles user_roles_pkey; Type: CONSTRAINT; Schema: public; Owner: workflowuser
--

ALTER TABLE ONLY public.user_roles
    ADD CONSTRAINT user_roles_pkey PRIMARY KEY (user_id, role_id);


--
-- Name: users users_pkey; Type: CONSTRAINT; Schema: public; Owner: workflowuser
--

ALTER TABLE ONLY public.users
    ADD CONSTRAINT users_pkey PRIMARY KEY (id);


--
-- Name: workflow_comment workflow_comment_pkey; Type: CONSTRAINT; Schema: public; Owner: workflowuser
--

ALTER TABLE ONLY public.workflow_comment
    ADD CONSTRAINT workflow_comment_pkey PRIMARY KEY (id);


--
-- Name: workflow_histories workflow_histories_pkey; Type: CONSTRAINT; Schema: public; Owner: workflowuser
--

ALTER TABLE ONLY public.workflow_histories
    ADD CONSTRAINT workflow_histories_pkey PRIMARY KEY (id);


--
-- Name: workflow_requests workflow_requests_pkey; Type: CONSTRAINT; Schema: public; Owner: workflowuser
--

ALTER TABLE ONLY public.workflow_requests
    ADD CONSTRAINT workflow_requests_pkey PRIMARY KEY (id);


--
-- Name: flyway_schema_history_s_idx; Type: INDEX; Schema: public; Owner: workflowuser
--

CREATE INDEX flyway_schema_history_s_idx ON public.flyway_schema_history USING btree (success);


--
-- Name: idx_history_changed_at; Type: INDEX; Schema: public; Owner: workflowuser
--

CREATE INDEX idx_history_changed_at ON public.workflow_histories USING btree (changed_at);


--
-- Name: idx_history_changed_by; Type: INDEX; Schema: public; Owner: workflowuser
--

CREATE INDEX idx_history_changed_by ON public.workflow_histories USING btree (changed_by_id);


--
-- Name: idx_history_workflow; Type: INDEX; Schema: public; Owner: workflowuser
--

CREATE INDEX idx_history_workflow ON public.workflow_histories USING btree (workflow_request_id);


--
-- Name: idx_workflow_requests_approved_by_id; Type: INDEX; Schema: public; Owner: workflowuser
--

CREATE INDEX idx_workflow_requests_approved_by_id ON public.workflow_requests USING btree (approved_by_id);


--
-- Name: idx_workflow_requests_created_at; Type: INDEX; Schema: public; Owner: workflowuser
--

CREATE INDEX idx_workflow_requests_created_at ON public.workflow_requests USING btree (created_at);


--
-- Name: idx_workflow_requests_reviewed_by_id; Type: INDEX; Schema: public; Owner: workflowuser
--

CREATE INDEX idx_workflow_requests_reviewed_by_id ON public.workflow_requests USING btree (reviewed_by_id);


--
-- Name: idx_workflow_requests_status; Type: INDEX; Schema: public; Owner: workflowuser
--

CREATE INDEX idx_workflow_requests_status ON public.workflow_requests USING btree (status);


--
-- Name: idx_workflow_requests_submitted_by_id; Type: INDEX; Schema: public; Owner: workflowuser
--

CREATE INDEX idx_workflow_requests_submitted_by_id ON public.workflow_requests USING btree (submitted_by_id);


--
-- Name: refresh_tokens fk1lih5y2npsf8u5o3vhdb9y0os; Type: FK CONSTRAINT; Schema: public; Owner: workflowuser
--

ALTER TABLE ONLY public.refresh_tokens
    ADD CONSTRAINT fk1lih5y2npsf8u5o3vhdb9y0os FOREIGN KEY (user_id) REFERENCES public.users(id);


--
-- Name: workflow_comment fk4yqf94klf39f5iphaa6puldvj; Type: FK CONSTRAINT; Schema: public; Owner: workflowuser
--

ALTER TABLE ONLY public.workflow_comment
    ADD CONSTRAINT fk4yqf94klf39f5iphaa6puldvj FOREIGN KEY (workflow_instance_id) REFERENCES public.workflow_requests(id);


--
-- Name: audit_logs fk6kkr1cx12j4vinn5ele5o88f4; Type: FK CONSTRAINT; Schema: public; Owner: workflowuser
--

ALTER TABLE ONLY public.audit_logs
    ADD CONSTRAINT fk6kkr1cx12j4vinn5ele5o88f4 FOREIGN KEY (performed_by_id) REFERENCES public.users(id);


--
-- Name: workflow_requests fk7vgssf7j7t5txc949s6pq05xs; Type: FK CONSTRAINT; Schema: public; Owner: workflowuser
--

ALTER TABLE ONLY public.workflow_requests
    ADD CONSTRAINT fk7vgssf7j7t5txc949s6pq05xs FOREIGN KEY (rejected_by_id) REFERENCES public.users(id);


--
-- Name: attachments fk9h1kopt5dm8v53doltmuog4i9; Type: FK CONSTRAINT; Schema: public; Owner: workflowuser
--

ALTER TABLE ONLY public.attachments
    ADD CONSTRAINT fk9h1kopt5dm8v53doltmuog4i9 FOREIGN KEY (workflow_request_id) REFERENCES public.workflow_requests(id);


--
-- Name: workflow_histories fkbrjh7hdttxb3u6wntlgmhsr39; Type: FK CONSTRAINT; Schema: public; Owner: workflowuser
--

ALTER TABLE ONLY public.workflow_histories
    ADD CONSTRAINT fkbrjh7hdttxb3u6wntlgmhsr39 FOREIGN KEY (workflow_request_id) REFERENCES public.workflow_requests(id);


--
-- Name: user_roles fkh8ciramu9cc9q3qcqiv4ue8a6; Type: FK CONSTRAINT; Schema: public; Owner: workflowuser
--

ALTER TABLE ONLY public.user_roles
    ADD CONSTRAINT fkh8ciramu9cc9q3qcqiv4ue8a6 FOREIGN KEY (role_id) REFERENCES public.roles(id);


--
-- Name: workflow_requests fkhemcmmqpf1cgao2bfih9a2kw; Type: FK CONSTRAINT; Schema: public; Owner: workflowuser
--

ALTER TABLE ONLY public.workflow_requests
    ADD CONSTRAINT fkhemcmmqpf1cgao2bfih9a2kw FOREIGN KEY (submitted_by_id) REFERENCES public.users(id);


--
-- Name: user_roles fkhfh9dx7w3ubf1co1vdev94g3f; Type: FK CONSTRAINT; Schema: public; Owner: workflowuser
--

ALTER TABLE ONLY public.user_roles
    ADD CONSTRAINT fkhfh9dx7w3ubf1co1vdev94g3f FOREIGN KEY (user_id) REFERENCES public.users(id);


--
-- Name: password_reset_tokens fkk3ndxg5xp6v7wd4gjyusp15gq; Type: FK CONSTRAINT; Schema: public; Owner: workflowuser
--

ALTER TABLE ONLY public.password_reset_tokens
    ADD CONSTRAINT fkk3ndxg5xp6v7wd4gjyusp15gq FOREIGN KEY (user_id) REFERENCES public.users(id);


--
-- Name: workflow_requests fkmdp6biy0qo19bk22gepqnx8dk; Type: FK CONSTRAINT; Schema: public; Owner: workflowuser
--

ALTER TABLE ONLY public.workflow_requests
    ADD CONSTRAINT fkmdp6biy0qo19bk22gepqnx8dk FOREIGN KEY (assigned_reviewer_id) REFERENCES public.users(id);


--
-- Name: workflow_requests fkmo177ov8drbgp8bbbrys3ypef; Type: FK CONSTRAINT; Schema: public; Owner: workflowuser
--

ALTER TABLE ONLY public.workflow_requests
    ADD CONSTRAINT fkmo177ov8drbgp8bbbrys3ypef FOREIGN KEY (assigned_approver_id) REFERENCES public.users(id);


--
-- Name: workflow_requests fkmopexgd1eats6s8siw2aflwcq; Type: FK CONSTRAINT; Schema: public; Owner: workflowuser
--

ALTER TABLE ONLY public.workflow_requests
    ADD CONSTRAINT fkmopexgd1eats6s8siw2aflwcq FOREIGN KEY (reviewed_by_id) REFERENCES public.users(id);


--
-- Name: workflow_requests fknyia3vv9dmlxrhb5rohd9bmym; Type: FK CONSTRAINT; Schema: public; Owner: workflowuser
--

ALTER TABLE ONLY public.workflow_requests
    ADD CONSTRAINT fknyia3vv9dmlxrhb5rohd9bmym FOREIGN KEY (approved_by_id) REFERENCES public.users(id);


--
-- Name: workflow_histories fkou86h39agoecjj72oxfe91j9d; Type: FK CONSTRAINT; Schema: public; Owner: workflowuser
--

ALTER TABLE ONLY public.workflow_histories
    ADD CONSTRAINT fkou86h39agoecjj72oxfe91j9d FOREIGN KEY (changed_by_id) REFERENCES public.users(id);


--
-- Name: workflow_comment fktcli0a5aoxw6mnksvklgxy68k; Type: FK CONSTRAINT; Schema: public; Owner: workflowuser
--

ALTER TABLE ONLY public.workflow_comment
    ADD CONSTRAINT fktcli0a5aoxw6mnksvklgxy68k FOREIGN KEY (commenter_id) REFERENCES public.users(id);


--
-- PostgreSQL database dump complete
--

\unrestrict RpdY4Mama83w77xhAr0Yj8MaJnIcjCz829I3w3Na9um7xY7TwseCiaj3bE9abgm

