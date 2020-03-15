-- :name create-user! :! :n
-- :doc creates a new user record
INSERT INTO users (first_name, last_name, email, password)
VALUES (:first_name, :last_name, :email, :password)

-- :name update-user! :! :n
-- :doc updates an existing user record
UPDATE users
SET first_name = :first_name, last_name = :last_name, email = :email
WHERE id = :id

-- :name get-user :? :1
-- :doc retrieves a user record given the id
SELECT *
FROM users
WHERE id = :id

-- :name get-user-by-email :? :1
-- :doc retrieves a user record given the email
SELECT *
FROM users
WHERE email = :email

-- :name delete-user! :! :n
-- :doc deletes a user record given the id
DELETE FROM users
WHERE id = :id

-- :name create-prediction! :! :n
-- :doc creates a new prediction record
INSERT INTO predictions (landmark, probability, image, user_id)
VALUES (:landmark, :probability, :image, :user_id)

-- :name update-prediction! :! :n
-- :doc updates an existing prediction record
UPDATE predictions
SET landmark = :landmark, probability = :probability, image = :image user_id = :user_id
WHERE id = :id

-- :name get-prediction :? :1
-- :doc retrieves a prediction record given the id
SELECT *
FROM predictions
WHERE id = :id

-- :name get-predictions-for-user :? :n
-- :doc retrieves prediction records given the user id
SELECT *
FROM predictions
WHERE user_id = :user_id

-- :name delete-prediction! :! :n
-- :doc deletes a prediction record given the id
DELETE FROM predictions
WHERE id = :id