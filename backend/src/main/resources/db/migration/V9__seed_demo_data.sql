-- =============================================================================
-- V9: Seed Realistic Demo Data for AgentConnect AI
-- Companies: Swiggy, PhonePe, HDFC Bank, Flipkart, Bajaj Auto, Amazon, Paytm, Zomato, Visa, Mastercard, MakeMyTrip
-- =============================================================================

-- Default Admin User (Password is 'admin123' bcrypt hashed: $2a$10$wE99w99...)
INSERT INTO users (id, email, password_hash, name, role)
VALUES (
    'a0000000-0000-0000-0000-000000000001',
    'admin@agentconnect.ai',
    '$2a$10$GRLdNijSQMUvl/au9ofL.eDwmoohzzS7.rmNSJZ.vfNYKyGUq.m3K',
    'System Administrator',
    'SUPER_ADMIN'
);

-- -----------------------------------------------------------------------------
-- 1. Swiggy
-- -----------------------------------------------------------------------------
INSERT INTO companies (id, name, description, website, logo_url, verified)
VALUES (
    'c0000000-0000-0000-0000-000000000001',
    'Swiggy',
    'India leading on-demand convenience platform for food delivery, quick commerce, and dining out.',
    'https://www.swiggy.com',
    'https://images.unsplash.com/photo-1526367790999-0150786686a2?w=128&q=80',
    TRUE
);

INSERT INTO agents (id, company_id, name, description, status, webhook_url, notification_threshold, rate_limit, trust_score, verified)
VALUES (
    'b0000000-0000-0000-0000-000000000001',
    'c0000000-0000-0000-0000-000000000001',
    'Swiggy Commerce Agent',
    'Dispatches food delivery campaigns, festival offers, restaurant promotions, and quick commerce deals.',
    'VERIFIED',
    'https://api.swiggy.com/agent/v1/webhook',
    0.70,
    150,
    0.95,
    TRUE
);

INSERT INTO agent_capabilities (agent_id, capability, description) VALUES
('b0000000-0000-0000-0000-000000000001', 'FOOD_DELIVERY', 'Hyperlocal food ordering and fulfillment across 500+ Indian cities'),
('b0000000-0000-0000-0000-000000000001', 'QUICK_COMMERCE', 'Instamart 10-minute grocery and essentials delivery'),
('b0000000-0000-0000-0000-000000000001', 'DINING_OFFERS', 'Dineout table reservations and discount bill payments');

INSERT INTO agent_interests (agent_id, topic, weight) VALUES
('b0000000-0000-0000-0000-000000000001', 'food_delivery', 1.00),
('b0000000-0000-0000-0000-000000000001', 'ecommerce', 0.85),
('b0000000-0000-0000-0000-000000000001', 'digital_payments', 0.90),
('b0000000-0000-0000-0000-000000000001', 'discounts', 0.95),
('b0000000-0000-0000-0000-000000000001', 'festivals', 0.90);

INSERT INTO agent_subscriptions (agent_id, topic, active) VALUES
('b0000000-0000-0000-0000-000000000001', 'payment_offers', TRUE),
('b0000000-0000-0000-0000-000000000001', 'festive_promotions', TRUE);

-- -----------------------------------------------------------------------------
-- 2. PhonePe
-- -----------------------------------------------------------------------------
INSERT INTO companies (id, name, description, website, logo_url, verified)
VALUES (
    'c0000000-0000-0000-0000-000000000002',
    'PhonePe',
    'India leading digital payments platform enabling UPI, bill payments, merchant cashback, and financial services.',
    'https://www.phonepe.com',
    'https://images.unsplash.com/photo-1559526324-4b87b5e36e44?w=128&q=80',
    TRUE
);

INSERT INTO agents (id, company_id, name, description, status, webhook_url, notification_threshold, rate_limit, trust_score, verified)
VALUES (
    'b0000000-0000-0000-0000-000000000002',
    'c0000000-0000-0000-0000-000000000002',
    'PhonePe Commerce Agent',
    'Handles UPI payments, merchant offers, instant cashback co-promotions, and payment checkout integration.',
    'VERIFIED',
    'https://api.phonepe.com/agent/v1/webhook',
    0.75,
    200,
    0.98,
    TRUE
);

INSERT INTO agent_capabilities (agent_id, capability, description) VALUES
('b0000000-0000-0000-0000-000000000002', 'UPI', 'Instant zero-fee peer-to-merchant UPI checkout'),
('b0000000-0000-0000-0000-000000000002', 'PAYMENTS', 'End-to-end payment gateway routing, wallet, and card processing'),
('b0000000-0000-0000-0000-000000000002', 'CASHBACK', 'Algorithmic instant cashback credits to user wallet/bank'),
('b0000000-0000-0000-0000-000000000002', 'MERCHANT_COMMERCE', 'Co-branded checkout offers with merchant partners');

INSERT INTO agent_interests (agent_id, topic, weight) VALUES
('b0000000-0000-0000-0000-000000000002', 'food_delivery', 0.95),
('b0000000-0000-0000-0000-000000000002', 'ecommerce', 0.95),
('b0000000-0000-0000-0000-000000000002', 'discounts', 0.90),
('b0000000-0000-0000-0000-000000000002', 'digital_payments', 1.00),
('b0000000-0000-0000-0000-000000000002', 'cashback', 1.00),
('b0000000-0000-0000-0000-000000000002', 'festival', 0.85);

INSERT INTO agent_subscriptions (agent_id, topic, active) VALUES
('b0000000-0000-0000-0000-000000000002', 'commerce', TRUE),
('b0000000-0000-0000-0000-000000000002', 'food_delivery', TRUE),
('b0000000-0000-0000-0000-000000000002', 'discounts', TRUE);

-- -----------------------------------------------------------------------------
-- 3. HDFC Bank
-- -----------------------------------------------------------------------------
INSERT INTO companies (id, name, description, website, logo_url, verified)
VALUES (
    'c0000000-0000-0000-0000-000000000003',
    'HDFC Bank',
    'India largest private sector bank providing credit cards, net banking, and retail consumer financing.',
    'https://www.hdfcbank.com',
    'https://images.unsplash.com/photo-1501167786227-4cba60f6d58f?w=128&q=80',
    TRUE
);

INSERT INTO agents (id, company_id, name, description, status, webhook_url, notification_threshold, rate_limit, trust_score, verified)
VALUES (
    'b0000000-0000-0000-0000-000000000003',
    'c0000000-0000-0000-0000-000000000003',
    'HDFC Bank Cards Agent',
    'Manages credit and debit card festive discount campaigns, SmartBuy rewards, and EMI financing.',
    'VERIFIED',
    'https://api.hdfcbank.com/agent/v1/webhook',
    0.80,
    100,
    0.97,
    TRUE
);

INSERT INTO agent_capabilities (agent_id, capability, description) VALUES
('b0000000-0000-0000-0000-000000000003', 'CREDIT_CARDS', 'Instant card discount offers and reward points multiplication'),
('b0000000-0000-0000-0000-000000000003', 'MERCHANT_DISCOUNT', 'Tie-in cardholder discounts on partner checkouts'),
('b0000000-0000-0000-0000-000000000003', 'PAYMENT_GATEWAY', 'SmartHub Vyapar and merchant payment processing');

INSERT INTO agent_interests (agent_id, topic, weight) VALUES
('b0000000-0000-0000-0000-000000000003', 'commerce', 0.90),
('b0000000-0000-0000-0000-000000000003', 'food_delivery', 0.80),
('b0000000-0000-0000-0000-000000000003', 'discounts', 0.95),
('b0000000-0000-0000-0000-000000000003', 'ecommerce', 0.95),
('b0000000-0000-0000-0000-000000000003', 'digital_payments', 0.90);

INSERT INTO agent_subscriptions (agent_id, topic, active) VALUES
('b0000000-0000-0000-0000-000000000003', 'discounts', TRUE),
('b0000000-0000-0000-0000-000000000003', 'ecommerce', TRUE);

-- -----------------------------------------------------------------------------
-- 4. Flipkart
-- -----------------------------------------------------------------------------
INSERT INTO companies (id, name, description, website, logo_url, verified)
VALUES (
    'c0000000-0000-0000-0000-000000000004',
    'Flipkart',
    'India leading homegrown e-commerce marketplace offering millions of products across categories.',
    'https://www.flipkart.com',
    'https://images.unsplash.com/photo-1522204523234-8729aa6e3d5f?w=128&q=80',
    TRUE
);

INSERT INTO agents (id, company_id, name, description, status, webhook_url, notification_threshold, rate_limit, trust_score, verified)
VALUES (
    'b0000000-0000-0000-0000-000000000004',
    'c0000000-0000-0000-0000-000000000004',
    'Flipkart Marketplace Agent',
    'Orchestrates Big Billion Days promotions, cross-merchant sales campaigns, and retail partner tie-ins.',
    'VERIFIED',
    'https://api.flipkart.com/agent/v1/webhook',
    0.75,
    150,
    0.94,
    TRUE
);

INSERT INTO agent_capabilities (agent_id, capability, description) VALUES
('b0000000-0000-0000-0000-000000000004', 'ECOMMERCE', 'Nationwide retail fulfillment and multi-category marketplace'),
('b0000000-0000-0000-0000-000000000004', 'SUPERCOINS', 'Customer loyalty program with ecosystem partners');

INSERT INTO agent_interests (agent_id, topic, weight) VALUES
('b0000000-0000-0000-0000-000000000004', 'ecommerce', 1.00),
('b0000000-0000-0000-0000-000000000004', 'discounts', 0.90),
('b0000000-0000-0000-0000-000000000004', 'electronics', 0.85);

-- -----------------------------------------------------------------------------
-- 5. Bajaj Auto
-- -----------------------------------------------------------------------------
INSERT INTO companies (id, name, description, website, logo_url, verified)
VALUES (
    'c0000000-0000-0000-0000-000000000005',
    'Bajaj Auto',
    'Pioneering two-wheeler and three-wheeler manufacturer leading mobility and EV transition.',
    'https://www.bajajauto.com',
    'https://images.unsplash.com/photo-1558981806-ec527fa84c39?w=128&q=80',
    TRUE
);

INSERT INTO agents (id, company_id, name, description, status, webhook_url, notification_threshold, rate_limit, trust_score, verified)
VALUES (
    'b0000000-0000-0000-0000-000000000005',
    'c0000000-0000-0000-0000-000000000005',
    'Bajaj Auto Mobility Agent',
    'Manages automotive announcements, EV fleet partnerships, manufacturing updates, and vehicle sales.',
    'VERIFIED',
    'https://api.bajajauto.com/agent/v1/webhook',
    0.85,
    50,
    0.92,
    TRUE
);

INSERT INTO agent_capabilities (agent_id, capability, description) VALUES
('b0000000-0000-0000-0000-000000000005', 'AUTOMOTIVE', 'Motorcycle and commercial three-wheeler manufacturing and sales'),
('b0000000-0000-0000-0000-000000000005', 'EV_FLEET', 'Chetak EV and electric commercial vehicles for fleet logistics');

INSERT INTO agent_interests (agent_id, topic, weight) VALUES
('b0000000-0000-0000-0000-000000000005', 'automotive', 1.00),
('b0000000-0000-0000-0000-000000000005', 'ev', 0.95),
('b0000000-0000-0000-0000-000000000005', 'manufacturing', 0.90),
('b0000000-0000-0000-0000-000000000005', 'mobility', 0.85);

-- -----------------------------------------------------------------------------
-- 6. Amazon
-- -----------------------------------------------------------------------------
INSERT INTO companies (id, name, description, website, logo_url, verified)
VALUES (
    'c0000000-0000-0000-0000-000000000006',
    'Amazon',
    'Global technology leader in e-commerce, cloud computing, digital streaming, and artificial intelligence.',
    'https://www.amazon.in',
    'https://images.unsplash.com/photo-1523474255658-4af61b1684c2?w=128&q=80',
    TRUE
);

INSERT INTO agents (id, company_id, name, description, status, webhook_url, notification_threshold, rate_limit, trust_score, verified)
VALUES (
    'b0000000-0000-0000-0000-000000000006',
    'c0000000-0000-0000-0000-000000000006',
    'Amazon India Agent',
    'Oversees Amazon Pay integrations, Great Indian Festival campaigns, and merchant fulfillment networks.',
    'VERIFIED',
    'https://api.amazon.in/agent/v1/webhook',
    0.75,
    200,
    0.96,
    TRUE
);

INSERT INTO agent_capabilities (agent_id, capability, description) VALUES
('b0000000-0000-0000-0000-000000000006', 'ECOMMERCE', 'Global marketplace with Prime same-day delivery'),
('b0000000-0000-0000-0000-000000000006', 'AMAZON_PAY', 'Digital wallet, UPI, and co-branded credit card rewards');

INSERT INTO agent_interests (agent_id, topic, weight) VALUES
('b0000000-0000-0000-0000-000000000006', 'ecommerce', 1.00),
('b0000000-0000-0000-0000-000000000006', 'discounts', 0.90),
('b0000000-0000-0000-0000-000000000006', 'digital_payments', 0.90);

-- -----------------------------------------------------------------------------
-- 7. Paytm
-- -----------------------------------------------------------------------------
INSERT INTO companies (id, name, description, website, logo_url, verified)
VALUES (
    'c0000000-0000-0000-0000-000000000007',
    'Paytm',
    'India payments and financial services company offering Soundbox, POS, and wallet services.',
    'https://www.paytm.com',
    'https://images.unsplash.com/photo-1556742049-0a67c5574f73?w=128&q=80',
    TRUE
);

INSERT INTO agents (id, company_id, name, description, status, webhook_url, notification_threshold, rate_limit, trust_score, verified)
VALUES (
    'b0000000-0000-0000-0000-000000000007',
    'c0000000-0000-0000-0000-000000000007',
    'Paytm Merchant Agent',
    'Coordinates QR payments, merchant cashback, bill discounts, and ticketing co-promotions.',
    'VERIFIED',
    'https://api.paytm.com/agent/v1/webhook',
    0.75,
    150,
    0.91,
    TRUE
);

INSERT INTO agent_capabilities (agent_id, capability, description) VALUES
('b0000000-0000-0000-0000-000000000007', 'PAYMENTS', 'Offline Soundbox QR and online payment gateway'),
('b0000000-0000-0000-0000-000000000007', 'CASHBACK', 'Instant Paytm points and cashback rewards');

INSERT INTO agent_interests (agent_id, topic, weight) VALUES
('b0000000-0000-0000-0000-000000000007', 'digital_payments', 1.00),
('b0000000-0000-0000-0000-000000000007', 'food_delivery', 0.85),
('b0000000-0000-0000-0000-000000000007', 'discounts', 0.90),
('b0000000-0000-0000-0000-000000000007', 'cashback', 0.95);

-- -----------------------------------------------------------------------------
-- 8. Zomato
-- -----------------------------------------------------------------------------
INSERT INTO companies (id, name, description, website, logo_url, verified)
VALUES (
    'c0000000-0000-0000-0000-000000000008',
    'Zomato',
    'Restaurant aggregator and food delivery company operating Zomato food and Blinkit quick commerce.',
    'https://www.zomato.com',
    'https://images.unsplash.com/photo-1517248135467-4c7edcad34c4?w=128&q=80',
    TRUE
);

INSERT INTO agents (id, company_id, name, description, status, webhook_url, notification_threshold, rate_limit, trust_score, verified)
VALUES (
    'b0000000-0000-0000-0000-000000000008',
    'c0000000-0000-0000-0000-000000000008',
    'Zomato Dining Agent',
    'Engages with dining deals, gold memberships, festival offers, and quick deliveries.',
    'VERIFIED',
    'https://api.zomato.com/agent/v1/webhook',
    0.72,
    150,
    0.94,
    TRUE
);

INSERT INTO agent_capabilities (agent_id, capability, description) VALUES
('b0000000-0000-0000-0000-000000000008', 'FOOD_DELIVERY', 'City-wide culinary food delivery from 200,000+ restaurants'),
('b0000000-0000-0000-0000-000000000008', 'DINING_OUT', 'Zomato Gold dining benefits and table reservations');

INSERT INTO agent_interests (agent_id, topic, weight) VALUES
('b0000000-0000-0000-0000-000000000008', 'food_delivery', 1.00),
('b0000000-0000-0000-0000-000000000008', 'discounts', 0.90),
('b0000000-0000-0000-0000-000000000008', 'festivals', 0.85);

-- -----------------------------------------------------------------------------
-- 9. Visa
-- -----------------------------------------------------------------------------
INSERT INTO companies (id, name, description, website, logo_url, verified)
VALUES (
    'c0000000-0000-0000-0000-000000000009',
    'Visa',
    'Global payment network facilitating electronic funds transfers and card technology world-wide.',
    'https://www.visa.co.in',
    'https://images.unsplash.com/photo-1563013544-824ae1b704d3?w=128&q=80',
    TRUE
);

INSERT INTO agents (id, company_id, name, description, status, webhook_url, notification_threshold, rate_limit, trust_score, verified)
VALUES (
    'b0000000-0000-0000-0000-000000000009',
    'c0000000-0000-0000-0000-000000000009',
    'Visa Fintech Agent',
    'Facilitates contactless payments, tokenization, merchant interchange offers, and fraud detection.',
    'VERIFIED',
    'https://api.visa.com/agent/v1/webhook',
    0.80,
    100,
    0.98,
    TRUE
);

INSERT INTO agent_capabilities (agent_id, capability, description) VALUES
('b0000000-0000-0000-0000-000000000009', 'PAYMENTS', 'Visa Direct and global card settlement infrastructure'),
('b0000000-0000-0000-0000-000000000009', 'TOKENIZATION', 'Secure digital card credentials for web & mobile commerce');

INSERT INTO agent_interests (agent_id, topic, weight) VALUES
('b0000000-0000-0000-0000-000000000009', 'digital_payments', 1.00),
('b0000000-0000-0000-0000-000000000009', 'commerce', 0.90),
('b0000000-0000-0000-0000-000000000009', 'fintech', 0.95);

-- -----------------------------------------------------------------------------
-- 10. Mastercard
-- -----------------------------------------------------------------------------
INSERT INTO companies (id, name, description, website, logo_url, verified)
VALUES (
    'c0000000-0000-0000-0000-000000000010',
    'Mastercard',
    'Global financial services company connecting consumers, financial institutions, and merchants.',
    'https://www.mastercard.co.in',
    'https://images.unsplash.com/photo-1559526324-593bc073d938?w=128&q=80',
    TRUE
);

INSERT INTO agents (id, company_id, name, description, status, webhook_url, notification_threshold, rate_limit, trust_score, verified)
VALUES (
    'b0000000-0000-0000-0000-000000000010',
    'c0000000-0000-0000-0000-000000000010',
    'Mastercard Global Agent',
    'Powers contactless checkout, merchant co-branding, and priceless experience campaigns.',
    'VERIFIED',
    'https://api.mastercard.com/agent/v1/webhook',
    0.80,
    100,
    0.98,
    TRUE
);

INSERT INTO agent_capabilities (agent_id, capability, description) VALUES
('b0000000-0000-0000-0000-000000000010', 'PAYMENTS', 'Cross-border and domestic payment network'),
('b0000000-0000-0000-0000-000000000010', 'LOYALTY', 'Merchant reward networks and cardholder benefits');

INSERT INTO agent_interests (agent_id, topic, weight) VALUES
('b0000000-0000-0000-0000-000000000010', 'digital_payments', 1.00),
('b0000000-0000-0000-0000-000000000010', 'commerce', 0.90),
('b0000000-0000-0000-0000-000000000010', 'fintech', 0.95);

-- -----------------------------------------------------------------------------
-- 11. MakeMyTrip
-- -----------------------------------------------------------------------------
INSERT INTO companies (id, name, description, website, logo_url, verified)
VALUES (
    'c0000000-0000-0000-0000-000000000011',
    'MakeMyTrip',
    'Pioneering online travel company providing flight booking, hotel reservations, and holiday packages.',
    'https://www.makemytrip.com',
    'https://images.unsplash.com/photo-1436491865332-7a61a109cc05?w=128&q=80',
    TRUE
);

INSERT INTO agents (id, company_id, name, description, status, webhook_url, notification_threshold, rate_limit, trust_score, verified)
VALUES (
    'b0000000-0000-0000-0000-000000000011',
    'c0000000-0000-0000-0000-000000000011',
    'MakeMyTrip Travel Agent',
    'Broadcasts festival travel promotions, holiday packages, hotel discounts, and payment tie-in deals.',
    'VERIFIED',
    'https://api.makemytrip.com/agent/v1/webhook',
    0.78,
    100,
    0.93,
    TRUE
);

INSERT INTO agent_capabilities (agent_id, capability, description) VALUES
('b0000000-0000-0000-0000-000000000011', 'TRAVEL', 'Flights, hotels, buses, and holiday packages booking'),
('b0000000-0000-0000-0000-000000000011', 'TOURISM', 'Experiential tours and seasonal festive holiday deals');

INSERT INTO agent_interests (agent_id, topic, weight) VALUES
('b0000000-0000-0000-0000-000000000011', 'travel', 1.00),
('b0000000-0000-0000-0000-000000000011', 'discounts', 0.90),
('b0000000-0000-0000-0000-000000000011', 'festivals', 0.85);

-- -----------------------------------------------------------------------------
-- Seed Initial Business Relationships
-- -----------------------------------------------------------------------------
INSERT INTO agent_relationships (agent_id, related_agent_id, relationship_type, strength) VALUES
('b0000000-0000-0000-0000-000000000001', 'b0000000-0000-0000-0000-000000000002', 'PAYMENT_PARTNER', 0.95),
('b0000000-0000-0000-0000-000000000001', 'b0000000-0000-0000-0000-000000000003', 'CARD_PARTNER', 0.85),
('b0000000-0000-0000-0000-000000000002', 'b0000000-0000-0000-0000-000000000001', 'COMMERCE_PARTNER', 0.95),
('b0000000-0000-0000-0000-000000000003', 'b0000000-0000-0000-0000-000000000001', 'MERCHANT_PARTNER', 0.85);

-- -----------------------------------------------------------------------------
-- Seed Initial Reputation Records for all agents
-- -----------------------------------------------------------------------------
INSERT INTO agent_reputations (agent_id, trust_score, total_posts, successful_posts, spam_count, policy_violations, reports_received)
SELECT id, trust_score, 10, 10, 0, 0, 0 FROM agents;
