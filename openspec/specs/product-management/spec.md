# product-management Specification

## Purpose
TBD - created by archiving change iteration-2. Update Purpose after archive.
## Requirements
### Requirement: Product CRUD

The system SHALL provide full CRUD operations for product management.

#### Scenario: Create product

- **WHEN** user submits product creation request
- **THEN** system validates required fields (product_name, product_type, unit)
- **AND** system checks product_code uniqueness
- **AND** product record is created in `product` table
- **AND** product ID is returned in response
- **AND** created_by and created_at are auto-filled

#### Scenario: Query product list

- **WHEN** user requests product list with pagination
- **THEN** system returns paginated product list
- **AND** supports filtering by product_name, product_code, category_id, product_type, status
- **AND** supports keyword search across product_name and product_code
- **AND** response includes product basic info without BOM and images

#### Scenario: Update product

- **WHEN** user submits product update request
- **THEN** system validates required fields
- **AND** system checks product_code uniqueness (if changed, excluding self)
- **AND** product record is updated in database
- **AND** updated_by and updated_at are auto-filled

#### Scenario: Delete product

- **WHEN** user submits product deletion request
- **THEN** system checks if product is referenced as material in any bom_item
- **AND** if referenced, system rejects deletion with message listing referencing products
- **AND** if not referenced, system performs logical deletion (sets `deleted` flag)
- **AND** associated images and bom_items are logically deleted

#### Scenario: Enable/disable product

- **WHEN** user changes product status
- **THEN** product status is updated in database
- **AND** disabled products are shown with disabled status indicator

### Requirement: Product detail view

The system SHALL provide a comprehensive product detail view.

#### Scenario: View product detail

- **WHEN** user opens product detail page
- **THEN** system displays product basic info in first tab
- **AND** system displays image gallery in second tab
- **AND** system displays BOM management in third tab (only for 半成品 and 成品)
- **AND** all tabs load data on demand (lazy loading)

### Requirement: Product category management

The system SHALL manage product categories with hierarchical structure.

#### Scenario: Create category

- **WHEN** user submits category creation request
- **THEN** system validates required fields (name)
- **AND** system accepts optional parent_id (default 0 for top-level)
- **AND** category record is created in `product_category` table

#### Scenario: Query category tree

- **WHEN** user requests category tree
- **THEN** system returns all categories as a tree structure
- **AND** top-level categories have parent_id = 0
- **AND** sub-categories are nested under their parent
- **AND** categories are sorted by sort_order

#### Scenario: Update category

- **WHEN** user submits category update request
- **THEN** system validates required fields
- **AND** category record is updated in database

#### Scenario: Delete category

- **WHEN** user submits category deletion request
- **THEN** system checks if category has child categories
- **AND** if has children, system rejects deletion
- **AND** system checks if category is referenced by any product
- **AND** if referenced, system rejects deletion
- **AND** if no children and no products, category is deleted

### Requirement: Product image management

The system SHALL support multiple images per product with primary image designation.

#### Scenario: Upload product image

- **WHEN** user uploads an image for a product
- **THEN** system validates file type (jpg, png, gif, webp)
- **AND** system validates file size (max 5MB)
- **AND** image file is stored in `uploads/product/` directory
- **AND** image record is created in `product_image` table with relative path
- **AND** if it is the first image, it is automatically set as primary

#### Scenario: List product images

- **WHEN** user views product images tab
- **THEN** system returns all images for the product
- **AND** images are sorted by sort_order, is_primary DESC
- **AND** primary image is visually highlighted

#### Scenario: Set primary image

- **WHEN** user sets an image as primary
- **THEN** system clears is_primary flag on all other images of the product
- **AND** sets is_primary = 1 on the selected image

#### Scenario: Delete product image

- **WHEN** user deletes a product image
- **THEN** system removes image record from database
- **AND** image file is deleted from disk
- **AND** if deleted image was primary, system auto-promotes next image as primary

### Requirement: Product data model

The system SHALL store product data with proper relational structure.

#### Scenario: Product table structure

- **WHEN** product record is created
- **THEN** table `product` contains:
  - id (BIGINT PK)
  - product_code (VARCHAR(50) UNIQUE)
  - product_name (VARCHAR(100))
  - category_id (BIGINT FK → product_category.id)
  - product_type (TINYINT: 1=原材料, 2=半成品, 3=成品)
  - spec (VARCHAR(200))
  - unit (VARCHAR(20))
  - weight (DECIMAL(10,3))
  - standard_cost (DECIMAL(12,2))
  - standard_price (DECIMAL(12,2))
  - status (TINYINT: 0=停用, 1=正常)
  - created_by, created_at, updated_by, updated_at, deleted, factory_id, remark

#### Scenario: Product category table structure

- **WHEN** category record is created
- **THEN** table `product_category` contains:
  - id (BIGINT PK)
  - name (VARCHAR(50))
  - parent_id (BIGINT DEFAULT 0)
  - sort_order (INT DEFAULT 0)
  - status (TINYINT DEFAULT 1)
  - created_by, created_at, updated_by, updated_at, deleted, factory_id, remark

#### Scenario: Product image table structure

- **WHEN** image record is created
- **THEN** table `product_image` contains:
  - id (BIGINT PK)
  - product_id (BIGINT FK → product.id)
  - image_url (VARCHAR(500))
  - sort_order (INT DEFAULT 0)
  - is_primary (TINYINT DEFAULT 0)
  - created_by, created_at, updated_by, updated_at, deleted, factory_id, remark

